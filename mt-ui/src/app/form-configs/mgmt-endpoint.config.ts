import { IForm } from 'mt-form-builder/lib/classes/template.interface';
import { HTTP_METHODS } from '../clazz/validation/aggregate/endpoint/interfaze-endpoint';

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
            "type": "paginated-select",
            "display": false,
            "label": "SELECT_CORS_PROFILE",
            "key": "corsProfile",
            "position": {
                "row": "9",
                "column": "0"
            },
            "options": [],
            required: true,
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
            "type": "paginated-select",
            "display": false,
            "label": "SELECT_CACHE_PROFILE",
            "key": "cacheProfile",
            "position": {
                "row": "12",
                "column": "0"
            },
            "options": [],
            required: false,
        },
    ],
}
