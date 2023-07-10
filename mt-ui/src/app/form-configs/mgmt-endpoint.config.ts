import { IForm } from 'mt-form-builder/lib/classes/template.interface';
import { HTTP_METHODS } from '../clazz/validation/endpoint.interface';

export const MGMT_EP_FORM_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "text",
            "display": false,
            "label": "ID",
            "key": "id",
            "position": {
                "row": "0",
                "column": "0"
            }
        },
        {
            "type": "paginated-select",
            "display": true,
            "label": "ENTER_RESOURCE_ID",
            "key": "resourceId",
            "position": {
                "row": "1",
                "column": "0"
            },
            options:[],
            required: true,
        },
        {
            "type": "text",
            "display": true,
            "label": "NAME",
            "key": "name",
            "position": {
                "row": "2",
                "column": "0"
            },
            required: true,
        },
        {
            "type": "text",
            "display": true,
            "label": "ENTER_DESCRIPTION",
            "key": "description",
            "position": {
                "row": "3",
                "column": "0"
            },
        },
        {
            "type": "text",
            "display": true,
            "label": "ENTER_ENDPOINT",
            "key": "path",
            "position": {
                "row": "4",
                "column": "0"
            },
            required: true,
        },
        {
            "type": "radio",
            "display": true,
            "label": "IS_WEBSOCKET",
            "key": "isWebsocket",
            "position": {
                "row": "5",
                "column": "0"
            },
            "options": [
                { label: 'NO', value: "no" },
                { label: 'YES', value: "yes" },
            ],
        },
        {
            "type": "select",
            "display": false,
            "label": "SELECT_METHOD",
            "key": "method",
            "position": {
                "row": "6",
                "column": "0"
            },
            "options": HTTP_METHODS,
            required: true,
        },
        {
            "type": "checkbox",
            "display": true,
            "label": "",
            "key": "csrf",
            "position": {
                "row": "7",
                "column": "0"
            },
            "options": [
                { label: 'CSRF_ENABLED', value: "true" },
            ],
        },
        {
            "type": "checkbox",
            "display": true,
            "label": "",
            "key": "cors",
            "position": {
                "row": "8",
                "column": "0"
            },
            "options": [
                { label: 'CORS_ENABLED', value: "true" },
            ],
        },
        {
            "type": "checkbox",
            "display": true,
            "label": "",
            "key": "secured",
            "position": {
                "row": "10",
                "column": "0"
            },
            "options": [
                { label: 'PROTECTED_ENDPOINT', value: "true" },
            ],
        },
        {
            "type": "checkbox",
            "display": true,
            "label": "",
            "key": "shared",
            "position": {
                "row": "11",
                "column": "0"
            },
            "options": [
                { label: 'SHARED_API', value: "true" },
            ],
        },
        {
            "type": "radio",
            "display": true,
            "label": "ENABLE_CACHE",
            "key": "allowCache",
            "position": {
                "row": "12",
                "column": "0"
            },
            "options": [
                { label: 'YES', value: "yes" },
                { label: 'NO', value: "no" },
            ],
        },
        {
            "type": "checkbox",
            "display": false,
            "label": "CACHE_CONTROL_SERVER_HEADER",
            "key": "cacheControl",
            "position": {
                "row": "13",
                "column": "0"
            },
            "options": [
                { label: 'MUST_REVALIDATE', value: "must-revalidate" },
                { label: 'NO_CACHE', value: "no-cache" },
                { label: 'NO_STORE', value: "no-store" },
                { label: 'NO_TRANSFOR', value: "no-transform" },
                { label: 'CACHE_CONTROL_PUBLIC', value: "public" },
                { label: 'CACHE_CONTROL_PRIVATE', value: "private" },
                { label: 'PROXY_REVALIDATE', value: "proxy-revalidate" },
                { label: 'CACHE_CONTROL_MAX_AGE', value: "max-age" },
                { label: 'SMAX_AGE', value: "s-maxage" },
            ],
        },
        {
            "type": "text",
            "display": false,
            "label": "MAX_AGE_HEADER",
            "key": "maxAgeValue",
            "position": {
                "row": "14",
                "column": "0"
            },
        },
        {
            "type": "text",
            "display": false,
            "label": "SMAX_AGE_HEADER",
            "key": "smaxAgeValue",
            "position": {
                "row": "15",
                "column": "0"
            },
        },
        {
            "type": "text",
            "display": false,
            "label": "VARY_HEADER",
            "key": "vary",
            "position": {
                "row": "16",
                "column": "0"
            },
        },
        {
            "type": "text",
            "display": false,
            "label": "EXPIRE_HEADER",
            "key": "expires",
            "position": {
                "row": "17",
                "column": "0"
            },
        },
        {
            "type": "checkbox",
            "display": false,
            "label": "",
            "key": "etagValidation",
            "position": {
                "row": "18",
                "column": "0"
            },
            "options": [
                { label: 'ENABLE_ETAG', value: "true" },
            ],
        },
        {
            "type": "checkbox",
            "display": false,
            "label": "",
            "key": "etagType",
            "position": {
                "row": "19",
                "column": "0"
            },
            "options": [
                { label: 'WEAK_VALIDATION', value: "true" },
            ],
        },
        {
            "type": "text",
            "display": true,
            "label": "MAX_AGE",
            "key": "corsMaxAge",
            "position": {
                "row": "20",
                "column": "0"
            },
        },
        {
            "type": "checkbox",
            "display": true,
            "label": "",
            "key": "allowCredentials",
            "position": {
                "row": "21",
                "column": "0"
            },
            "options": [
                { label: 'ALLOW_CREDENTIAL', value: "true" },
            ],
        },
    ],
}