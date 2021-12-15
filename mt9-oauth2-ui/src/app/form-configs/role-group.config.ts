import { IForm } from 'mt-form-builder/lib/classes/template.interface';

export const FORM_CONFIG: IForm = {
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
            "type": "text",
            "display": true,
            "label": "NAME",
            "key": "name",
            required:true,
            "position": {
                "row": "1",
                "column": "0"
            },
        },
        {
            "type": "text",
            "display": true,
            "label": "DESCRIPTION",
            "key": "description",
            "position": {
                "row": "2",
                "column": "0"
            },
        },
        {
            "type": "radio",
            "display": true,
            "label": "PLEASE_LIMIT_ACCESS",
            "key": "allowType",
            "position": {
                "row": "14",
                "column": "0"
            },
            "options": [
                { label: 'NONE', value: "BOTH" },
                { label: 'EP_USER_ONLY', value: "USER" },
                { label: 'EP_CLIENT_ONLY', value: "CLIENT" },
            ],
        },
        {
            "type": "paginated-select",
            "display": false,
            "label": "EP_CLIENT_ROLES",
            "key": "clientRoles",
            multiple:true,
            "position": {
                "row": "15",
                "column": "0"
            },
            "options": [],
        },
        {
            "type": "paginated-select",
            "display": false,
            multiple:true,
            "label": "EP_USER_ROLES",
            "key": "userRoles",
            "position": {
                "row": "17",
                "column": "0"
            },
            "options": [],
        },
    ],
}
