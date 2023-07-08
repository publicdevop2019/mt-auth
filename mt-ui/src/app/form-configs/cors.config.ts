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
            "display": true,
            "label": "NAME",
            "key": "name",
        },
        {
            "type": "text",
            "display": true,
            "label": "DESCRIPTION",
            "key": "description",
        },
        {
            "type": "text",
            "display": true,
            "label": "MAX_AGE",
            "key": "maxAge",
        },
        {
            "type": "checkbox",
            "display": true,
            "label": "",
            "key": "allowCredentials",
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
        },
    ],
}
