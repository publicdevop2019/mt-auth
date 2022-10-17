import { IForm } from 'mt-form-builder/lib/classes/template.interface';

export const FORM_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "text",
            "display": true,
            "label": "PLS_ENTER_PROJECT_NAME",
            "key": "projectName",
            "position": {
                "row": "0",
                "column": "0"
            },
            required:true,
        },
    ],
}
