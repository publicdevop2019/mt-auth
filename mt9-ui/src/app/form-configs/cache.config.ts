import { IForm } from 'mt-form-builder/lib/classes/template.interface';
import { TYPE_ROLE_ENUM } from '../clazz/validation/aggregate/role/interface-role';

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
            "key": "name",
            required:true,
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
            "type": "radio",
            "display": true,
            "label": "ENABLE_CACHE",
            "key": "allowCache",
            "position": {
                "row": "3",
                "column": "0"
            },
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
            "position": {
                "row": "4",
                "column": "0"
            },
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
            "position": {
                "row": "5",
                "column": "0"
            },
        },
        {
            "type": "text",
            "display": false,
            "label": "SMAX_AGE_HEADER",
            "key": "smaxAgeValue",
            "position": {
                "row": "6",
                "column": "0"
            },
        },
        {
            "type": "text",
            "display": false,
            "label": "VARY_HEADER",
            "key": "vary",
            "position": {
                "row": "7",
                "column": "0"
            },
        },
        {
            "type": "text",
            "display": false,
            "label": "EXPIRE_HEADER",
            "key": "expires",
            "position": {
                "row": "8",
                "column": "0"
            },
        },
        {
            "type": "checkbox",
            "display": false,
            "label": "",
            "key": "etagValidation",
            "position": {
                "row": "9",
                "column": "0"
            },
            "options": [
                { label: 'ENABLE_ETAG', value: "true" },
            ],
        },
        {
            "type": "checkbox",
            "display": false,
            "label": "",
            "key": "etagType",
            "position": {
                "row": "10",
                "column": "0"
            },
            "options": [
                { label: 'WEAK_VALIDATION', value: "true" },
            ],
        },
    ],
}
