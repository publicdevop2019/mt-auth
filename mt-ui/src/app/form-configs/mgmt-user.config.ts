import { IForm } from 'mt-form-builder/lib/classes/template.interface';

export const FORM_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "text",
            "display": true,
            disabled:true,
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
            "type": "text",
            "display": true,
            "disabled": true,
            "label": "CREATED_AT",
            "key": "createdAt",
            "readonly": true,
            "position": {
                "row": "2",
                "column": "0"
            },
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
    ],
}
