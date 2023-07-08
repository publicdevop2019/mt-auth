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
            required:true,
        },
        {
            "type": "text",
            "display": true,
            "label": "DESCRIPTION",
            "key": "description",
        },
        {
            "type": "radio",
            "display": true,
            "label": "ENABLE_CACHE",
            "key": "allowCache",
            "options": [
                { label: 'YES', value: "yes" },
                { label: 'NO', value: "no" },
            ],
        },
        {
            "type": "checkbox",
            "display": false,
            "label": "CACHE_CONTROL_SERVER_HEADER",
            "key": "cacheControl",
            "options": [
                { label: 'MUST_REVALIDATE', value: "must-revalidate" },
                { label: 'NO_CACHE', value: "no-cache" },
                { label: 'NO_STORE', value: "no-store" },
                { label: 'NO_TRANSFOR', value: "no-transform" },
                { label: 'CACHE_CONTROL_PUBLIC', value: "public" },
                { label: 'CACHE_CONTROL_PRIVATE', value: "private" },
                { label: 'PROXY_REVALIDATE', value: "proxy-revalidate" },
                { label: 'CACHE_CONTROL_MAX_AGE', value: "max-age" },
                { label: 'SMAX_AGE', value: "s-maxage" },
            ],
        },
        {
            "type": "text",
            "display": false,
            "label": "MAX_AGE_HEADER",
            "key": "maxAgeValue",
        },
        {
            "type": "text",
            "display": false,
            "label": "SMAX_AGE_HEADER",
            "key": "smaxAgeValue",
        },
        {
            "type": "text",
            "display": false,
            "label": "VARY_HEADER",
            "key": "vary",
        },
        {
            "type": "text",
            "display": false,
            "label": "EXPIRE_HEADER",
            "key": "expires",
        },
        {
            "type": "checkbox",
            "display": false,
            "label": "",
            "key": "etagValidation",
            "options": [
                { label: 'ENABLE_ETAG', value: "true" },
            ],
        },
        {
            "type": "checkbox",
            "display": false,
            "label": "",
            "key": "etagType",
            "options": [
                { label: 'WEAK_VALIDATION', value: "true" },
            ],
        },
    ],
}
