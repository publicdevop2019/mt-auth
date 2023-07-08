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
        },
        {
            "type": "text",
            "display": true,
            "disabled": true,
            "label": "Enter email",
            "key": "email",
            "readonly": true,
        },
        {
            "type": "text",
            "display": true,
            "disabled": true,
            "label": "CREATED_AT",
            "key": "createdAt",
            "readonly": true,
        },
        {
            "type": "checkbox",
            "display": true,
            "label": "LOCK_OR_UNLOCK_USER",
            "key": "locked",
            "options": [
                { label: 'LOCK', value: "Lock" },
            ],
        },
    ],
}
