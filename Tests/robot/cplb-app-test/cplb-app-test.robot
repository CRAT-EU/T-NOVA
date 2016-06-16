*** Settings ***
Documentation     Test suite to verify LB application is OK
Suite Teardown    Delete All Sessions
Library           RequestsLibrary
Variables         Variables.py


*** Test Cases ***
Get Load Balancer application
    [Documentation]    Get Load Balancer application
    Create Session    session    http://${LB_APP_IP}:${LB_APP_PORT}
    ${resp}    RequestsLibrary.Get    session    ${CONTEXT}
    Log    ${resp.content}
    Should Be Equal As Strings    ${resp.status_code}    200
