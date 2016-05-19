*** Settings ***
Documentation     Test suite to verify Restconf is OK
Suite Teardown    Delete All Sessions
Library           RequestsLibrary
Variables         Variables.py


*** Test Cases ***
Get Cluster Controller one Modules
    [Documentation]    Get the controller modules via Restconf
    Create Session    session    http://${CONTROLLER1}:${RESTCONFPORT}    auth=${AUTH}    headers=${HEADERS_XML}
    ${resp}    RequestsLibrary.Get    session    ${REST_CONTEXT}
    Log    ${resp.content}
    Should Be Equal As Strings    ${resp.status_code}    200
    Should Contain    ${resp.content}    rolemanager


*** Test Cases ***
Get Cluster Controller two Modules
    [Documentation]    Get the controller modules via Restconf
    Create Session    session    http://${CONTROLLER2}:${RESTCONFPORT}    auth=${AUTH}    headers=${HEADERS_XML}
    ${resp}    RequestsLibrary.Get    session    ${REST_CONTEXT}
    Log    ${resp.content}
    Should Be Equal As Strings    ${resp.status_code}    200
    Should Contain    ${resp.content}    rolemanager

*** Test Cases ***
Get Cluster Controller three Modules
    [Documentation]    Get the controller modules via Restconf
    Create Session    session    http://${CONTROLLER3}:${RESTCONFPORT}    auth=${AUTH}    headers=${HEADERS_XML}
    ${resp}    RequestsLibrary.Get    session    ${REST_CONTEXT}
    Log    ${resp.content}
    Should Be Equal As Strings    ${resp.status_code}    200
    Should Contain    ${resp.content}    rolemanager
