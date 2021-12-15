import { IForm } from 'mt-form-builder/lib/classes/template.interface';
import { CATALOG_TYPE } from '../clazz/constants';

export const FORM_SEARCH_CATALOG_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "paginated-select",
            "display": true,
            "label": "SELECT_AN_ATTRIBUTE",
            "key": "searchByAttr",
            required:true,
            "position": {
                "row": "0",
                "column": "0"
            },
            "options":[]
        },
        {
            "type": "select",
            "display": false,
            "label": "SELECT_AN_ATTRIBUTE_VALUE",
            "key": "searchByAttrSelect",
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
            "key": "searchByAttrManual",
            "position": {
                "row": "0",
                "column": "1"
            },
            required:true,
        },
    ],
}