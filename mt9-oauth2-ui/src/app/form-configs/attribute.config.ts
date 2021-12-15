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
            "label": "ENTER_NAME",
            "key": "name",
            "position": {
                "row": "1",
                "column": "0"
            },
            required:true,
        },
        {
            "type": "text",
            "display": true,
            "label": "ENTER_DESCRIPTION",
            "key": "description",
            "position": {
                "row": "2",
                "column": "0"
            }
        },
        {
            "type": "radio",
            "display": true,
            "label": "ENTER_METHOD",
            "key": "method",
            "position": {
                "row": "3",
                "column": "0"
            },
            required:true,
            "options": [
                { label: 'MANUAL', value: "MANUAL" },
                { label: 'SELECT', value: "SELECT" },
            ],
        },
        {
            "type": "select",
            "display": true,
            "label": "SELECT_ATTR_TYPE",
            "key": "type",
            "position": {
                "row": "4",
                "column": "0"
            },
            required:true,
            "options": [
                { label: 'KEY_ATTR', value: "KEY_ATTR" },
                { label: 'SALES_ATTR', value: "SALES_ATTR" },
                { label: 'PROD_ATTR', value: "PROD_ATTR" },
                { label: 'GEN_ATTR', value: "GEN_ATTR" },
            ],
        },
    ],
}
export const FORM_CONFIG_ATTR_VALUE: IForm = {
    "repeatable": true,
    "inputs": [
        {
            "type": "text",
            "display": true,
            "label": "ENTER_VALUE",
            "key": "attrValue",
            required:true,
            "position": {
                "row": "0",
                "column": "0"
            }
        },
    ],
}
