import { IForm } from 'mt-form-builder/lib/classes/template.interface';
export const FORM_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "radio",
            "display": true,
            "label": "SELECT_BATCH_TYPE",
            "key": "type",
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
            required:true,
            "options": [
            ],
        },
        {
            "type": "paginated-select",
            "display": false,
            "label": "SELECT_CACHE_PROFILE",
            "key": "cacheId",
            required:true,
            "options": [
            ],
        },
    ],
}
