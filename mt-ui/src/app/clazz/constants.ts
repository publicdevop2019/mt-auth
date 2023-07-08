import { IOption } from 'mt-form-builder/lib/classes/template.interface';

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
export const TABLE_SETTING_KEY='displayColumns'