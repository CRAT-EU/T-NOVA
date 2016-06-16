# Global variables
CONTROLLER1 = '127.0.0.1'
CONTROLLER2 = '127.0.0.1'
CONTROLLER3 = '127.0.0.1'
RESTCONFPORT = '8181'
AUTH = [u'admin', u'admin']
HEADERS_XML = {'Content-Type': 'application/json'}
SET_ROLE_REST_CONTEXT = '/restconf/operations/rolemanager:set-switch-role'
GET_ROLE_REST_CONTEXT = '/restconf/operations/rolemanager:get-switch-role'
OF13_SWITCH_ID = "1"
SET_ROLE_DATA = '{"input":{"ofp-role":"BECOMEEQUAL","switch-ids":["'+OF13_SWITCH_ID+'"]}}'
GET_ROLE_DATA = '{"input":{"switch-ids":[]}}'
