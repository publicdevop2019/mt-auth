import { IForm } from 'mt-form-builder/lib/classes/template.interface';
import { HTTP_METHODS } from '../misc/constant';

export const CATALOG_FORM_CONFIG: IForm = {
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
            "type": "radio",
            "display": true,
            "label": "IS_EXTERNAL",
            "key": "isExternal",
            "position": {
                "row": "1",
                "column": "0"
            },
            direction:'row',
            "options": [
                { label: 'YES', value: "yes" },
                { label: 'NO', value: "no" },
            ],
        },
        {
            "type": "radio",
            "display": true,
            disabled:true,
            "label": "IS_SHARED",
            "key": "isShared",
            "position": {
                "row": "3",
                "column": "0"
            },
            direction:'row',
            "options": [
                { label: 'YES', value: "yes" },
                { label: 'NO', value: "no" },
            ],
        },
        {
            "type": "radio",
            "display": true,
            disabled:true,
            "label": "IS_SECURED",
            "key": "isSecured",
            "position": {
                "row": "2",
                "column": "0"
            },
            direction:'row',
            "options": [
                { label: 'YES', value: "yes" },
                { label: 'NO', value: "no" },
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
            "position": {
                "row": "0",
                "column": "0"
            },
            required: true,
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
            "type": "radio",
            "display": true,
            "label": "IS_WEBSOCKET",
            "key": "isWebsocket",
            required: true,
            "position": {
                "row": "2",
                "column": "0"
            },
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
            "position": {
                "row": "3",
                "column": "0"
            },
            required: true,
        },
        {
            "type": "select",
            "display": false,
            "label": "SELECT_METHOD",
            "key": "method",
            "position": {
                "row": "4",
                "column": "0"
            },
            "options": HTTP_METHODS,
            required: true,
        },
        {
            "type": "text",
            "display": true,
            "label": "ENTER_DESCRIPTION",
            "key": "description",
            "position": {
                "row": "5",
                "column": "0"
            },
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
            "position": {
                "row": "0",
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
                "row": "1",
                "column": "0"
            },
            "options": [
                { label: 'CORS_ENABLED', value: "true" },
            ],
        },
        {
            "type": "paginated-select",
            "display": false,
            "label": "SELECT_CORS_PROFILE",
            "key": "corsProfile",
            "position": {
                "row": "2",
                "column": "0"
            },
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
            "position": {
                "row": "0",
                "column": "0"
            },
            "options": [],
        },
        {
            "type": "text",
            "display": false,
            "label": "REPLENISH_RATE",
            "key": "replenishRate",
            required: true,
            "position": {
                "row": "1",
                "column": "0"
            },
        },
        {
            "type": "text",
            "display": false,
            "label": "BURST_CAPACITY",
            "key": "burstCapacity",
            required: true,
            "position": {
                "row": "2",
                "column": "0"
            },
        },
    ],
}