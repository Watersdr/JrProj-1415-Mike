#!/usr/bin/python

from base64 import b64decode
from firebase import firebase
import json
import os
import requests
import sys
import traceback

VERSION = 0.1
firebase_url = 'https://henry-test.firebaseio.com'

def initialize(projectID,github_username,opsys):
    # github API 3.0
    base_url = 'https://api.github.com/repos/RHITJuniorProjects/JrProj-1415-Mike/contents/Code/Platform/Hooks'
    if opsys == 'unix':
        base_url = base_url + '/Unix'
    else:
        base_url = base_url + '/Windows'

    sh_url = base_url+'/commit-msg'
    py_url = base_url+'/henry/commit.py'

    hook_dir = os.getcwd()+'/.git/hooks'
    henry_dir = hook_dir+'/henry'
    sh_path = hook_dir+'/commit-msg'
    py_path = henry_dir+'/commit.py'

    if not inGitRepo():
        print 'HENRY Error: Must be in the root of a Git repository'
        exit()

    # retrieve and decode the shell script
    r = requests.get(sh_url)
    sh = b64decode(json.loads(r.text)['content'])

    # retrieve and decode the python script
    r = requests.get(py_url)
    py = b64decode(json.loads(r.text)['content'])
    py = '\n'.join([fillTemplate(line,projectID,github_username) for line in py.split('\n')])

    # write the shell script
    with open(sh_path,'w') as f:
        f.write(sh)

    if not os.path.exists(henry_dir):
        os.makedirs(henry_dir)

    # write the python script
    with open(py_path,'w') as f:
        f.write(py)


def fillTemplate(line,projectID,github_username):
    if line.startswith('projectID = '):
        return "projectID = '"+projectID+"'"
    elif line.startswith('githubID = '):
        return "githubID = '"+github_username+"'"
    else:
        return line

    
def usage():
    print 'HENRY Error: invalid command'
    print
    print 'usage: henry <command> [<args>]' 
    print
    print 'The most commonly used henry commands are:'
    print '   init        Conect the current git repository to Henry'
    print '   version     Display the installed version number'
    print '   help        Display the help menu'


def helpmenu():
    if not inGitRepo():
        print 'Henry can only be used within a Git reposity'
        print 'Change directory to a Git repository or create a new one with'
        print '   git init'
        print
    print 'Connect a Git repository to Henry with'
    print '   henry init <project name> <github username>'


def version():
    print 'henry version',str(VERSION)


def status():
    if not inGitRepo():
        print 'This is not a Git repository, Henry cannot be initialized here'
    elif os.path.isfile(os.getcwd()+'/.git/hooks/commit-msg'):
        print 'This is a Henry Repository'
    else:
        print 'This Git repository is not yet connected to Henry'
        print 'Connect it with'
        print '   henry init <project name> <github username>'


def inGitRepo():
    try:
        with open(os.getcwd()+'/.git/hooks/commit-msg','w') as f:
            pass
        return True
    except IOError:
        return False


def getProjectID(project):
    ref = firebase.FirebaseApplication(firebase_url,None)
    path = '/projects'
    projects = ref.get(path,None)
    projects_with_names = {p:projects[p] for p in projects if 'name' in projects[p]}
    try:
        projectID = [p for p in projects_with_names if projects_with_names[p]['name'] == project][0]
    except:
        raise Exception('HENRY: Invalid or nonexistant project name')
    return projectID

def getUserID(github_username):
    ref = firebase.FirebaseApplication(firebase_url,None)
    path = '/users'
    users = ref.get(path,None)
    filteredusers = {u:users[u] for u in users if 'github' in users[u]}
    try:
        userID = [u for u in filteredusers if filteredusers[u]['github'] == github_username][0]
    except:
        raise Exception('HENRY: Invalid username')
    return userID


if __name__ == '__main__':
    try:
	    if len(sys.argv) == 1:
		usage()
	    elif sys.argv[1] == 'init':
		try:
		    pID = getProjectID(sys.argv[2]) 
		except:
		    print 'HENRY Error: Invalid or nonexistant Henry project name'
		    print '   henry init <project name> <github username>'
		    exit(1)
		try:
		    github_username = sys.argv[3]
		    userID = getUserID(github_username)
		except:
		    print 'HENRY Error: Invalid Github username'
		    print '   henry init <project name> <github username>'
		    exit(1)
		initialize(pID,github_username,'windows')
	    elif sys.argv[1] in {'version','-version','--version','-v'}:
		version()
	    elif sys.argv[1] in {'help','-help','--help','-h'}:
		helpmenu()
	    elif sys.argv[1] in {'status'}:
		status()
	    else:
		usage()

	    """
	    This line causes the script to exit abruptly and bypass all
	    exit handlers. This is necessary because the Python Firebase
	    API does not play nice with the Windows Python Multithreading
	    library. Maybe someday they will fix it and this won't be
	    necessary.

	    """
    except Exception as e:
        #traceback.print_exc(file=sys.stdout)
	os._exit(0)
    os._exit(0)
