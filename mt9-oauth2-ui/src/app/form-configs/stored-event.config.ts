import { IForm } from 'mt-form-builder/lib/classes/template.interface';
export const FORM_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "select",
            "display": true,
            "label": "PLEASE_SELECT_APP_NAME",
            "key": "appName",
            "position": {
                "row": "0",
                "column": "0"
            },
            "options": [
                { label: 'SAGA_SVC', value: "/saga-svc" },
                { label: 'MALL_SVC', value: "/product-svc" },
                { label: 'PROFILE_SVC', value: "/profile-svc" },
                { label: 'PAYMENT_SVC', value: "/payment-svc" },
            ],
        },
    ],
}
