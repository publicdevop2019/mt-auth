export const GRANT_TYPE_LIST = [
    { label: 'CLIENT_CREDENTIALS', value: "CLIENT_CREDENTIALS" },
    { label: 'PASSWORD', value: "PASSWORD" },
    { label: 'AUTHORIZATION_CODE', value: "AUTHORIZATION_CODE" },
]
export const GRANT_TYPE_LIST_EXT = [
    ...GRANT_TYPE_LIST,
    { label: 'REFRESH_TOKEN', value: "REFRESH_TOKEN" },
]
export const RESOURCE_CLIENT_ROLE_LIST = [
    { label: 'ROLE_BACKEND', value: "0R8G09CBKU0X" },
    { label: 'ROLE_FIRST_PARTY', value: "0R8G09A7Q60W" },
]