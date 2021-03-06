import test_base
import util

class UserTableTestCase(test_base.HenryTestCase):
    
    def test_user_creation(self):
        util.createUser(self.ref,'Test User','test_user@gmail.com',150)
        expected_data = {u'email': u'test_user@gmail.com', u'name': u'Test User'}
        path = '/users/simplelogin:150'
        actual_data = self.ref.get(path,None)
        self.assertEqual(expected_data,actual_data)

    def test_add_user_to_project(self):
        uid = 'simplelogin:100'
        project = 'Henry - Platform'
        pid = util.getProjectID(self.ref,project)
        util.addMember(self.ref,pid,uid,"Developer")
        projects = util.getProjects(self.ref,uid)
        self.assertTrue(pid in projects)
