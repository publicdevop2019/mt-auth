import { IForm } from 'mt-form-builder/lib/classes/template.interface';

export const FORM_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "imageUpload",
            "display": true,
            "label": "AVATAR",
            "key": "avatar",
        },
        {
            "type": "text",
            "display": true,
            "label": "USERNAME",
            "key": "username",
        },
        {
            "type": "select",
            "display": true,
            "label": "COUNTRY_CODE",
            "key": "mobileCountryCode",
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
            required: true,
        },
        {
            "type": "select",
            "display": true,
            "label": "PREF_LANGUAGE",
            "key": "language",
            options: [
                { label: 'ENGLISH', value: 'ENGLISH' },
                { label: 'CHINESE', value: 'MANDARIN' }
            ]
        },
    ],
}
