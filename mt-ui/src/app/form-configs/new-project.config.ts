import { IForm } from 'mt-form-builder/lib/classes/template.interface';

export const FORM_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "text",
            "display": true,
            "label": "NAME",
            "key": "projectName",
            "position": {
                "row": "0",
                "column": "0"
            },
            required:true,
        },
    ],
}
