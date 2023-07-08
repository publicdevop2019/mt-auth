import { IForm } from 'mt-form-builder/lib/classes/template.interface';

export const FORM_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "text",
            "display": false,
            "label": "ID",
            "key": "id",
        },
        {
            "type": "text",
            "display": false,
            "label": "",
            "key": "projectId",
        },
        {
            "type": "text",
            "display": true,
            "disabled": true,
            "label": "Enter email",
            "key": "email",
            "readonly": true,
        },
    ],
}
