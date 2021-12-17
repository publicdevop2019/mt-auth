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
            "disabled": true,
            "label": "Enter email",
            "key": "email",
            "readonly": true,
            "position": {
                "row": "1",
                "column": "0"
            },
        },
        {
            "type": "checkbox",
            "display": true,
            "label": "PLEASE_SELECT_AUTHORITY(S)",
            "key": "authority",
            "position": {
                "row": "5",
                "column": "0"
            },
            "options": [],
        },
        {
            "type": "checkbox",
            "display": true,
            "label": "LOCK_OR_UNLOCK_USER",
            "key": "locked",
            "position": {
                "row": "6",
                "column": "0"
            },
            "options": [
                { label: 'LOCK', value: "Lock" },
            ],
        },
        {
            "type": "checkbox",
            "display": true,
            "label": "",
            "key": "subNewOrder",
            "position": {
                "row": "7",
                "column": "0"
            },
            "options": [
                { label: 'SUBSCRIBE_FOR_NEW_ORDER', value: "Subscribe for new order" },
            ],
        },
    ],
}
