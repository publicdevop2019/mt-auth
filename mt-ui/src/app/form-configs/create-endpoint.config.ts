import { IForm } from 'mt-form-builder/lib/classes/template.interface';
import { HTTP_METHODS } from '../clazz/validation/aggregate/endpoint/interfaze-endpoint';

export const CATALOG_FORM_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "text",
            "display": false,
            "label": "ID",
            "key": "id",
        },
        {
            "type": "radio",
            "display": true,
            "label": "IS_EXTERNAL",
            "key": "isExternal",
            direction:'row',
            "options": [
                { label: 'NO', value: "no" },
                { label: 'YES', value: "yes" },
            ],
        },
        {
            "type": "radio",
            "display": true,
            "label": "IS_SHARED",
            "key": "isShared",
            direction:'row',
            "options": [
                { label: 'NO', value: "no" },
                { label: 'YES', value: "yes" },
            ],
        },
        {
            "type": "radio",
            "display": true,
            // disabled:true,//this has a bug
            "label": "IS_SECURED",
            "key": "isSecured",
            direction:'row',
            "options": [
                { label: 'NO', value: "no" },
                { label: 'YES', value: "yes" },
            ],
        },
    ],
}
export const BASIC_FORM_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "text",
            "display": true,
            "label": "NAME",
            "key": "name",
            required: true,
        },
        {
            "type": "paginated-select",
            "display": true,
            "label": "ENTER_RESOURCE_ID",
            "key": "resourceId",
            options:[],
            required: true,
        },
        {
            "type": "radio",
            "display": true,
            "label": "IS_WEBSOCKET",
            "key": "isWebsocket",
            "options": [
                { label: 'NO', value: "no" },
                { label: 'YES', value: "yes" },
            ],
        },
        {
            "type": "text",
            "display": true,
            "label": "ENTER_ENDPOINT",
            "key": "path",
            required: true,
        },
        {
            "type": "select",
            "display": false,
            "label": "SELECT_METHOD",
            "key": "method",
            "options": HTTP_METHODS,
            required: true,
        },
        {
            "type": "text",
            "display": true,
            "label": "ENTER_DESCRIPTION",
            "key": "description",
        },
    ],
}
export const SECURE_FORM_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "checkbox",
            "display": true,
            "label": "",
            "key": "csrf",
            "options": [
                { label: 'CSRF_ENABLED', value: "true" },
            ],
        },
        {
            "type": "checkbox",
            "display": true,
            "label": "",
            "key": "cors",
            "options": [
                { label: 'CORS_ENABLED', value: "true" },
            ],
        },
        {
            "type": "paginated-select",
            "display": false,
            "label": "SELECT_CORS_PROFILE",
            "key": "corsProfile",
            "options": [],
            required: true,
        },
    ],
}
export const PERFORMANCE_FORM_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "paginated-select",
            "display": false,
            "label": "SELECT_CACHE_PROFILE",
            "key": "cacheProfile",
            "options": [],
        },
        {
            "type": "text",
            "display": false,
            "label": "REPLENISH_RATE",
            "key": "replenishRate",
        },
        {
            "type": "text",
            "display": false,
            "label": "BURST_CAPACITY",
            "key": "burstCapacity",
        },
    ],
}
