import { IForm } from 'mt-form-builder/lib/classes/template.interface';
export const ATTR_SALES_FORM_CONFIG: IForm = {
    "repeatable": true,
    "inputs": [
        {
            "type": "text",
            "display": true,
            "label": "ENTER_INITIAL_ORDER_STORAGE",
            "key": "storageOrder",
            "position": {
                "row": "1",
                "column": "0"
            }
        },
        {
            "type": "text",
            "display": false,
            "label": "ENTER_ORDER_STORAGE_INCREASE_AMOUNT",
            "key": "storage_OrderIncreaseBy",
            "position": {
                "row": "1",
                "column": "1"
            }
        },
        {
            "type": "text",
            "display": false,
            "label": "ENTER_ORDER_STORAGE_DECREASE_AMOUNT",
            "key": "storage_OrderDecreaseBy",
            "position": {
                "row": "1",
                "column": "2"
            }
        },
        {
            "type": "text",
            "display": true,
            "label": "ENTER_INITIAL_ACTUAL_STORAGE",
            "key": "storageActual",
            "position": {
                "row": "2",
                "column": "0"
            }
        },
        {
            "type": "text",
            "display": false,
            "label": "ENTER_ACTUAL_STORAGE_INCREASE_AMOUNT",
            "key": "storage_ActualIncreaseBy",
            "position": {
                "row": "2",
                "column": "1"
            }
        },
        {
            "type": "text",
            "display": false,
            "label": "ENTER_ACTUAL_STORAGE_DECREASE_AMOUNT",
            "key": "storage_ActualDecreaseBy",
            "position": {
                "row": "2",
                "column": "2"
            }
        },
        {
            "type": "text",
            "display": true,
            "label": "ENTER_PRICE",
            "key": "price",
            "position": {
                "row": "3",
                "column": "0"
            },
            required:true,
        },
        {
            "type": "text",
            "display": true,
            "label": "ENTER_SALES",
            "key": "sales",
            "position": {
                "row": "4",
                "column": "0"
            },
        },
        {
            "type": "form",
            "display": true,
            "label": "",
            "key": "attrSalesFormChild",
            "form": {
                "repeatable": true,
                "inputs": [
                    {
                        "type": "select",
                        "display": true,
                        "label": "SELECT_AN_SALES_ATTRIBUTE",
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
                    }
                ]
            },
            "position": {
                "row": "0",
                "column": "0"
            }
        },

    ],
}
