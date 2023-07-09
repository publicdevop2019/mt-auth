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
            "display": false,
            "label": "",
            "key": "projectId",
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
    ],
}