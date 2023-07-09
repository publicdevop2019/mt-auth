import { IForm } from 'mt-form-builder/lib/classes/template.interface';

export const FORM_TABLE_COLUMN_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "checkbox",
            "display": true,
            "label": "SELECT_TO_CHANGE_TABLE_COLUMN",//required, or value will be boolean
            "key": "displayColumns",
            "position": {
                "row": "0",
                "column": "0"
            },
            "options":[]
        },
    ],
}