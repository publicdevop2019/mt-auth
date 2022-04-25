import { IForm } from 'mt-form-builder/lib/classes/template.interface';

export const FORM_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "imageUpload",
            "display": true,
            "label": "AVATAR",
            "key": "avatar",
            "position": {
                "row": "0",
                "column": "0"
            },
        },
        {
            "type": "text",
            "display": true,
            "label": "USERNAME",
            "key": "username",
            "position": {
                "row": "1",
                "column": "0"
            },
        },
        {
            "type": "select",
            "display": true,
            "label": "COUNTRY_CODE",
            "key": "mobileCountryCode",
            "position": {
                "row": "2",
                "column": "0"
            },
            options: [
                { label: '+1', value: '1' },
                { label: '+86', value: '86' }
            ],
            required: true,
        },
        {
            "type": "text",
            "display": true,
            "label": "MOBILE_NUMBER",
            "key": "mobileNumber",
            "position": {
                "row": "2",
                "column": "1"
            },
            required: true,
        },
        {
            "type": "select",
            "display": true,
            "label": "PREF_LANGUAGE",
            "key": "language",
            "position": {
                "row": "3",
                "column": "0"
            },
            options: [
                { label: 'ENGLISH', value: 'ENGLISH' },
                { label: 'CHINESE', value: 'MANDARIN' }
            ]
        },
    ],
}
