import { IForm } from 'mt-form-builder/lib/classes/template.interface';
export const FORM_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "radio",
            "display": true,
            "label": "SELECT_BATCH_TYPE",
            "key": "type",
            "position": {
                "row": "0",
                "column": "0"
            },
            required:true,
            "options": [
                { label: 'BATCH_CORS', value: "cors" },
                { label: 'BATCH_CACHE', value: "cache" },
            ],
        },
        {
            "type": "paginated-select",
            "display": false,
            "label": "SELECT_CORS_PROFILE",
            "key": "corsId",
            "position": {
                "row": "1",
                "column": "0"
            },
            required:true,
            "options": [
            ],
        },
        {
            "type": "paginated-select",
            "display": false,
            "label": "SELECT_CACHE_PROFILE",
            "key": "cacheId",
            "position": {
                "row": "1",
                "column": "0"
            },
            required:true,
            "options": [
            ],
        },
    ],
}