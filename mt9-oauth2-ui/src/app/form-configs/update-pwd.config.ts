import { IForm } from 'mt-form-builder/lib/classes/template.interface';

export const FORM_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "text",
            "sensitive":true,
            "autocomplete":'current-password',
            "display": true,
            "label": "ENTER_YOUR_CURRENT_PASSWORD",
            "key": "currentPwd",
            "position": {
                "row": "2",
                "column": "0"
            },
            required:true,
        },
        {
            "type": "text",
            "display": true,
            "sensitive":true,
            "autocomplete":'new-password',
            "label": "ENTER_YOUR_NEW_PASSWORD",
            "key": "pwd",
            "position": {
                "row": "3",
                "column": "0"
            },
            required:true,
        },
        {
            "type": "text",
            "sensitive":true,
            "display": true,
            "autocomplete":'new-password',
            "label": "REENTER_YOUR_NEW_PASSWORD",
            "key": "confirmPwd",
            "position": {
                "row": "4",
                "column": "0"
            },
            required:true,
        },
    ],
}
