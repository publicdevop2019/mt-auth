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
            "label": "ENTER_TITLE",
            "key": "name",
            "position": {
                "row": "1",
                "column": "0"
            },
            required:true,
        },
        {
            "type": "paginated-select",
            "display": false,
            "label": "ENTER_PARENT_ID",
            "key": "parentId",
            "position": {
                "row": "3",
                "column": "0"
            },
            "options":[]
        },
        {
            "type": "radio",
            "display": true,
            "label": "PLEASE_SELECT_TYPE",
            "key": "catalogType",
            "position": {
                "row": "2",
                "column": "0"
            },
            "options": [
                { label: 'CATALOG_FRONTEND', value: "FRONTEND" },
                { label: 'CATALOG_BACKEND', value: "BACKEND" },
            ],
        },
    ],
}
export const CATALOG_ATTR_FORM_CONFIG: IForm = {
    "repeatable": true,
    "inputs": [
        {
            "type": "paginated-select",
            "display": true,
            "label": "SELECT_AN_ATTRIBUTE",
            "key": "attributeId",
            "position": {
                "row": "0",
                "column": "0"
            },
            "options": [
            ],
            required:true,
        },
        {
            "type": "select",
            "display": false,
            "label": "SELECT_AN_ATTRIBUTE_VALUE",
            "key": "attributeValueSelect",
            "position": {
                "row": "0",
                "column": "1"
            },
            "options": [
            ],
            required:true,
        },
        {
            "type": "text",
            "display": false,
            "label": "ENTER_AN_ATTRIBUTE_VALUE",
            "key": "attributeValueManual",
            "position": {
                "row": "0",
                "column": "1"
            },
            required:true,
        },
    ],
}