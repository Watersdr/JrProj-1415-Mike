#!/usr/bin/python
import sys
import re
import subprocess
import time
from firebase import firebase


##
#   These fields are populated by the initializer
##
projectID = '-JYcg488tAYS5rJJT4Kh'
githubID = 'aj-michael'

prodUrl = 'https://henry-production.firebaseio.com'
stagUrl = 'https://henry-staging.firebaseio.com'
testUrl = 'https://henry-test.firebaseio.com'


def readCommit(path):
    with open(path,'r') as msgfile:
        msg = msgfile.read()
    return msg.strip()
    

def getLoC():
    command = 'git diff --cached --shortstat'.split(' ')
    pipe = subprocess.Popen(command,stdout=subprocess.PIPE)
    pair = pipe.communicate()[0]
    vals = pair.replace(' ','').split(',') 
    if len(vals) == 3:
        vals = vals[1:]
    elif len(vals) == 2:
        vals = [vals[1]]+['0'] if 'insertion' in pair else ['0']+[vals[1]]
    elif len(vals) == 1:
        raise Exception('HENRY: Unexpected output of `git diff --cached --shortstat`')
    else:
        vals = ['0','0']
    nums = map(lambda x: int(x[0]),vals) 
    return nums[0] - nums[1]


def parse(msg):
    hoursRE = r"\[hours:([0-9]+)\]"
    milestoneRE = r"\[milestone:([^\]]*)\]"
    taskRE = r"\[task:([^\]]*)\]"
    statusRE = r"\[status:([^\]]*)\]"

    rexs = [hoursRE,milestoneRE,taskRE,statusRE]
    results = [re.search(rex,msg) for rex in rexs]
    results = [res.group(1) if res != None else None for res in results]
    return results


def getEmail():
    command = 'git config --global user.email'.split(' ')
    pipe = subprocess.Popen(command,stdout=subprocess.PIPE)
    return pipe.communicate()[0].strip()
    

def getUserID(gituser,ref):
    users = ref.get('/users',None)
    filteredusers = {u:users[u] for u in users if 'github' in users[u]}
    try:
        userID = [u for u in filteredusers if filteredusers[u]['github']==gituser][0]
    except:
        raise Exception('HENRY: Invalid username, commit failed')
    return userID


getTime = lambda: int(time.time()*1000)


def getMilestoneID(projectID,milestone):
    path = '/projects/'+projectID+'/milestones'
    milestones = ref.get(path,None)
    filtered = {m:milestones[m] for m in milestones if 'name' in milestones[m]}
    try:
        mID = [m for m in filtered if filtered[m]['name']==milestone][0]
    except:
        raise Exception('HENRY: Nonexistent milestone, commit failed')
    return mID


def getTaskID(projectID,milestoneID,task):
    path = '/projects/'+projectID+'/milestones/'+milestoneID+'/tasks'
    tasks = ref.get(path,None)
    filtered = {t:tasks[t] for t in tasks if 'name' in tasks[t]}
    try:
        tID = [t for t in filtered if filtered[t]['name']==task][0]
    except:
        raise Exception('HENRY: Nonexistent task, commit failed')
    return tID


def writeCommit(ref,msg,project,uid,hours,status,loc,ts,projectID,milestoneID,taskID):
    path = '/commits/'+projectID+'/'
    try:
        result = ref.post(path,{
            'hours':hours,
            'user':uid,
            'lines_of_code':loc,
            'message':msg,
            'timestamp':ts,
            'milestone':milestoneID,
            'task':taskID,
            'status':status,
            'project':projectID
        })
    except:
        raise Exception('Connection to Firebase commits table denied')
    return result.values()[0]


def addCommitToUser(ref,uid,commitid):
    path = '/users/' + uid + '/commits'
    try:
        ref.patch(path,{commitid:commitid})
    except:
        raise Exception('Connection to Firebase users table denied')


def addCommitToProject(ref,projectid,commitid):
    path = '/projects/'+projectid+'/commits'
    try:
        ref.patch(path,{commitid:commitid})
    except:
        raise Exception('Connection to Firebase projects table denied')
   

def getActiveMilestones(ref,userID,projectID):
    path = '/projects/'+projectID+'/milestones'
    milestones = ref.get(path,None)
    filtered = {m:milestones[m]['name'] for m in milestones if 'name' in milestones[m]}
    return filtered


def getAssignedTasks(ref,userID,projectID,milestoneID):
    path = '/users/'+userID+'/projects/'+projectID+'/milestones/'+milestoneID+'/tasks'
    taskIDs = ref.get(path,None).keys()
    path = '/projects/'+projectID+'/milestones/'+milestoneID+'/tasks'
    allTasks = ref.get(path,None)
    assignedTasks = {tID:allTasks[tID]['name'] for tID in taskIDs}
    return assignedTasks

    
def promptAsNecessary(ref,userID,projectID,hours,milestone,task,status):
    # mac/linux
    #sys.stdin = open('/dev/tty')

    # windows
    sys.stdin = open('CON')

    if hours == None:
        sys.stdout.write('Hours: ')
        sys.stdout.flush()
        hours = raw_input()

    # prompt for milestone if necessary
    if milestone == None:
        print 'Active milestones:'
	sys.stdout.flush()
        idsByIndex = [] ; index = 1
        for mID, mName in getActiveMilestones(ref,userID,projectID).iteritems():
            print ' - '+str(index)+'. '+mName
            idsByIndex = idsByIndex + [mID]
            index = index + 1
	sys.stdout.write('Milestone: ')
	sys.stdout.flush()
        milestone = raw_input()
        if milestone.isdigit() and int(milestone) <= len(idsByIndex):
            mID = idsByIndex[int(milestone)-1]
        else:
            mID = getMilestoneID(projectID,milestone)
    else:
        mID = getMilestoneID(projectID,milestone)

    # prompt for task if necessary
    if task == None:
        print 'Tasks assigned to you:'
        idsByIndex = [] ; index = 1
        for tID, tName in getAssignedTasks(ref,userID,projectID,mID).iteritems():
            print ' - '+str(index)+'. '+tName
            idsByIndex = idsByIndex + [tID]
            index = index + 1
	sys.stdout.write('Task: ')
	sys.stdout.flush()
        task = raw_input()
        if task.isdigit() and int(task) <= len(idsByIndex):
            tID = idsByIndex[int(task)-1]
        else:
            tID = getTaskID(projectID,mID,task)
    else:
        tID = getTaskID(projectID,mID,task)

    # prompt for status if necessary
    if status == None:
        print 'Select status from:' # New, Implementation, Testing, Verify, Regression, Closed'
	possibleStatuses = ['New', 'Implementation', 'Testing', 'Verify', 'Regression', 'Closed']
	for i in range(len(possibleStatuses)):
            print ' - ' + str(i + 1) + ': ', possibleStatuses[i]
	sys.stdout.write('Status: ')
	sys.stdout.flush()
        status = raw_input()
        if status.isdigit() and (int(status) <= len(possibleStatuses)):
	    status = possibleStatuses[int(status) - 1]
		
    return hours,mID,tID,status


if __name__ == '__main__':
    # set this to the correct database
    ref = firebase.FirebaseApplication(testUrl, None)

    email = getEmail()
    userID = getUserID(githubID,ref)
    msg = readCommit(sys.argv[1])
    [hours,milestone,task,status],loc = parse(msg),getLoC()
    ts = getTime()
    hours,milestoneID,taskID,status = promptAsNecessary(ref,userID,projectID,hours,milestone,task,status)

    commitID =writeCommit(ref,msg,None,userID,int(hours),status,loc,ts,projectID,milestoneID,taskID)
    addCommitToProject(ref,projectID,commitID)
    addCommitToUser(ref,userID,commitID)

    #raise Exception('Reached end, prevents commit from executing')