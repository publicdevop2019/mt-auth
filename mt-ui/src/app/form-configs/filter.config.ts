import { IForm } from 'mt-form-builder/lib/classes/template.interface';
import { CATALOG_TYPE } from '../clazz/constants';
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
            "label": "DESCRIPTION",
            "key": "description",
            "position": {
                "row": "1",
                "column": "0"
            }
        },
    ],
}
export const FORM_CATALOG_CONFIG: IForm = {
    "repeatable": true,
    "inputs": [
        {
            "type": "paginated-select",
            "display": true,
            "label": "ENTER_LINKED_CATALOG_IDS",
            "key": "catalogId",
            required:true,
            queryPrefix:CATALOG_TYPE.FRONTEND,
            "position": {
                "row": "0",
                "column": "0"
            },
            "options":[]
        },
    ],
}
export const FORM_FILTER_ITEM_CONFIG: IForm = {
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
            "type": "form",
            "display": true,
            "label": "",
            "key": "filterForm",
            "form": {
                "repeatable": true,
                "inputs": [
                    {
                        "type": "text",
                        "display": true,
                        "label": "ENTER_VALUE",
                        "key": "value",
                        "position": {
                            "row": "0",
                            "column": "0"
                        }
                    },
                ]
            },
            "position": {
                "row": "3",
                "column": "0"
            }
        },
    ],
}
