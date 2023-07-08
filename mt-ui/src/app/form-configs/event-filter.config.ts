import { IForm } from "mt-form-builder/lib/classes/template.interface";

export const FORM_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "radio",
            "display": true,
            "label": "",
            "key": "filterBy",
            direction:'row',
            "options": [
                { label: 'ALL_TYPE', value: 'all' },
                { label: 'AUDIT_TYPE', value: 'audit' },
                { label: 'REJECTED_TYPE', value: 'rejected' },
                { label: 'ROUTE_TYPE', value: 'unroutable' }
            ],
        },
    ],
}