import { IOption } from 'mt-form-builder/lib/classes/template.interface';

export const CONST_DTX_STATUS: IOption[] = [
    { label: 'STARTED', value: "STARTED" },
    { label: 'PENDING', value: "PENDING" },
    { label: 'SUCCESS', value: "SUCCESS" },
    { label: 'RESOLVED', value: "RESOLVED" },
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
export const CONST_ATTR_TYPE: IOption[] = [
    { label: 'KEY_ATTR', value: "KEY_ATTR" },
    { label: 'SALES_ATTR', value: "SALES_ATTR" },
    { label: 'PROD_ATTR', value: "PROD_ATTR" },
    { label: 'GEN_ATTR', value: "GEN_ATTR" },
]
export const CATALOG_TYPE = {
    BACKEND: 'type:BACKEND',
    FRONTEND: 'type:FRONTEND'
}
export const ORDER_STATUS : IOption[]= [
    { label: 'NOT_PAID_RESERVED', value: "NOT_PAID_RESERVED" },
    { label: 'NOT_PAID_RECYCLED', value: "NOT_PAID_RECYCLED" },
    { label: 'PAID_RESERVED', value: "PAID_RESERVED" },
    { label: 'PAID_RECYCLED', value: "PAID_RECYCLED" },
    { label: 'CONFIRMED', value: "CONFIRMED" },
    { label: 'CANCELLED', value: "CANCELLED" },
]