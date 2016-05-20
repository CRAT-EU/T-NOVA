*** Settings ***
Documentation     Test suite to verify ODL's role manager features are ok
Suite Teardown    Delete All Sessions
Library           RequestsLibrary
Variables         Variables.py


*** Test Cases ***
Set role on cluster's controller1 instance and get it to check
    [Documentation]    Set and get controller role
    Create Session    session    http://${CONTROLLER1}:${RESTCONFPORT}    auth=${AUTH}    headers=${HEADERS_XML}
    ${resp}=    Post    session    ${SET_ROLE_REST_CONTEXT}    data=${SET_ROLE_DATA}
    Log    ${resp.content}
    Should Be Equal As Strings    ${resp.status_code}    200
    Create Session    session    http://${CONTROLLER1}:${RESTCONFPORT}    auth=${AUTH}    headers=${HEADERS_XML}
    ${resp}=    Post    session    ${GET_ROLE_REST_CONTEXT}    data=${GET_ROLE_DATA}
    Log    ${resp.content}
    Should Be Equal As Strings    ${resp.status_code}    200
    Should Contain    ${resp.content}    1:1:OFPCRROLEEQUAL


*** Test Cases ***
Set role on cluster's controller2 instance and get it to check
    [Documentation]    Set and get controller role
    Create Session    session    http://${CONTROLLER2}:${RESTCONFPORT}    auth=${AUTH}    headers=${HEADERS_XML}
    ${resp}=    Post    session    ${SET_ROLE_REST_CONTEXT}    data=${SET_ROLE_DATA}
    Log    ${resp.content}
    Should Be Equal As Strings    ${resp.status_code}    200
    Create Session    session    http://${CONTROLLER1}:${RESTCONFPORT}    auth=${AUTH}    headers=${HEADERS_XML}
    ${resp}=    Post    session    ${GET_ROLE_REST_CONTEXT}    data=${GET_ROLE_DATA}
    Log    ${resp.content}
    Should Be Equal As Strings    ${resp.status_code}    200
    Should Contain    ${resp.content}    1:1:OFPCRROLEEQUAL


*** Test Cases ***
Set role on cluster's controller3 instance and get it to check
    [Documentation]    Set and get controller role
    Create Session    session    http://${CONTROLLER3}:${RESTCONFPORT}    auth=${AUTH}    headers=${HEADERS_XML}
    ${resp}=    Post    session    ${SET_ROLE_REST_CONTEXT}    data=${SET_ROLE_DATA}
    Log    ${resp.content}
    Should Be Equal As Strings    ${resp.status_code}    200
    Create Session    session    http://${CONTROLLER1}:${RESTCONFPORT}    auth=${AUTH}    headers=${HEADERS_XML}
    ${resp}=    Post    session    ${GET_ROLE_REST_CONTEXT}    data=${GET_ROLE_DATA}
    Log    ${resp.content}
    Should Be Equal As Strings    ${resp.status_code}    200
    Should Contain    ${resp.content}    1:1:OFPCRROLEEQUAL
