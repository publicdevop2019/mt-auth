import { IOption } from "./interface"

export const GRANT_TYPE_LIST = [
    { label: 'CLIENT_CREDENTIALS', value: "CLIENT_CREDENTIALS" },
    { label: 'PASSWORD', value: "PASSWORD" },
    { label: 'AUTHORIZATION_CODE', value: "AUTHORIZATION_CODE" },
]
export const GRANT_TYPE_LIST_EXT = [
    ...GRANT_TYPE_LIST,
    { label: 'REFRESH_TOKEN', value: "REFRESH_TOKEN" },
]
export const HTTP_METHODS = [
    { label: 'HTTP_GET', value: "GET" },
    { label: 'HTTP_POST', value: "POST" },
    { label: 'HTTP_PUT', value: "PUT" },
    { label: 'HTTP_DELETE', value: "DELETE" },
    { label: 'HTTP_PATCH', value: "PATCH" },
]
export const CONST_GRANT_TYPE: IOption[] = [
    { label: 'CLIENT_CREDENTIALS', value: "CLIENT_CREDENTIALS" },
    { label: 'PASSWORD', value: "PASSWORD" },
    { label: 'AUTHORIZATION_CODE', value: "AUTHORIZATION_CODE" },
    { label: 'REFRESH_TOKEN', value: "REFRESH_TOKEN" }
]
export const CONST_HTTP_METHOD: IOption[] = [
    { label: 'HTTP_GET', value: "GET" },
    { label: 'HTTP_POST', value: "POST" },
    { label: 'HTTP_PUT', value: "PUT" },
    { label: 'HTTP_DELETE', value: "DELETE" },
    { label: 'HTTP_PATCH', value: "PATCH" },
]
export const TABLE_SETTING_KEY = 'displayColumns'
export const APP_CONSTANT = {
    MT_AUTH_ACCESS_PATH: 'auth-svc',
    MGMT_RESOURCE_CLIENT_DROPDOWN: '/auth-svc/mgmt/clients/dropdown?query=resourceIndicator:1',
    TENANT_RESOURCE_CLIENT_DROPDOWN: '/clients/dropdown?query=resourceIndicator:1',
}
export enum grantTypeEnums {
    refresh_token = 'REFRESH_TOKEN',
    password = 'PASSWORD',
    client_credentials = 'CLIENT_CREDENTIALS',
    authorization_code = 'AUTHORIZATION_CODE'
}
export enum CLIENT_TYPE {
    backend_app = 'BACKEND_APP',
    frontend_app = 'FRONTEND_APP'
}
export const RESOURCE_NAME = {
    CACHE: 'cache',
    CORS: 'cors',
    ADMINS: 'admins',
    PERMISSIONS: 'permissions',
    ROLES: 'roles',
    ENDPOINTS: 'endpoints',
    CLIENTS: 'clients',
    USERS: 'users',
    SUBSCRIPTIONS_REQUEST: 'subscriptions/requests',
    SHARED_PERMISSION: 'permissions/shared',
    SHARED_ENDPOINTS: 'endpoints/shared',
    SUBSCRIPTIONS: 'subscriptions',
    MGMT_USERS: 'users',
    MGMT_EVENTS: 'events',
    MGMT_EVENTS_AUDIT: 'events/audit',
    MGMT_REVOKE_TOKEN: 'revoke-tokens',
    MGMT_PROJECTS: 'projects',
    MGMT_NOTIFICATION: 'notifications',
    MGMT_BELL_NOTIFICATION: 'notifications/bell',
    MGMT_CLIENTS: 'clients',
    MGMT_ENDPOINTS: 'endpoints',
    USER_BELL_NOTIFICATION: 'notifications/bell',
}