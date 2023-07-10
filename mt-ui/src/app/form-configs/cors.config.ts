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
            "label": "NAME",
            required: true,
            "key": "name",
            "position": {
                "row": "1",
                "column": "0"
            },
        },
        {
            "type": "text",
            "display": true,
            "label": "DESCRIPTION",
            "key": "description",
            "position": {
                "row": "2",
                "column": "0"
            },
        },
        {
            "type": "text",
            "display": true,
            "label": "MAX_AGE",
            "key": "maxAge",
            "position": {
                "row": "7",
                "column": "0"
            },
        },
        {
            "type": "checkbox",
            "display": true,
            "label": "",
            "key": "allowCredentials",
            "position": {
                "row": "8",
                "column": "0"
            },
            "options": [
                { label: 'ALLOW_CREDENTIAL', value: "true" },
            ],
        },
    ],
}
export const ORIGIN_FORM_CONFIG: IForm = {
    "repeatable": true,
    "inputs": [
        {
            "type": "text",
            "display": true,
            "label": "",
            "key": "allowOrigin",
            "position": {
                "row": "0",
                "column": "0"
            }
        },
    ],
}
export const ALLOWED_HEADERS_FORM_CONFIG: IForm = {
    "repeatable": true,
    "inputs": [
        {
            "type": "text",
            "display": true,
            "label": "",
            "key": "allowedHeaders",
            "position": {
                "row": "0",
                "column": "0"
            }
        },
    ],
}
export const EXPOSED_HEADERS_FORM_CONFIG: IForm = {
    "repeatable": true,
    "inputs": [
        {
            "type": "text",
            "display": true,
            "label": "",
            "key": "exposedHeaders",
            "position": {
                "row": "0",
                "column": "0"
            }
        },
    ],
}