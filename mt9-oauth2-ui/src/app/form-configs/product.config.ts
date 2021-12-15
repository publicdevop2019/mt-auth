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
            "type": "select",
            "display": true,
            "label": "PLEASE_SELECT_BACKEND_CATALOG",
            "key": "selectBackendCatalog",
            "position": {
                "row": "1",
                "column": "0"
            },
            required:true,
            "options": [],
        },
        {
            "type": "text",
            "display": true,
            "disabled": true,
            "label": "INHERIT_ATTR",
            "key": "attributesKey",
            "position": {
                "row": "2",
                "column": "0"
            },
            required:true,
        },
        {
            "type": "text",
            "display": true,
            "label": "ENTER_NAME",
            "key": "name",
            "position": {
                "row": "3",
                "column": "0"
            },
            required:true,
        },
        {
            "type": "imageUpload",
            "display": true,
            "label": "UPLOAD_PRODUCT_IMAGE_SMALL",
            "key": "imageUrlSmall",
            "position": {
                "row": "4",
                "column": "0"
            },
            required:true,
        },
        {
            "type": "text",
            "display": true,
            "label": "ENTER_DESCRIPTION",
            "key": "description",
            "position": {
                "row": "6",
                "column": "0"
            },
        },
        {
            "type": "radio",
            "display": true,
            "label": "PUBLISH_AFTER_CREATE",
            "key": "status",
            "position": {
                "row": "7",
                "column": "0"
            },
            "options": [
                { label: 'YES', value: "AVAILABLE" },
                { label: 'NO', value: "UNAVAILABLE" },
            ],
            required:true,
        },
        {
            "type": "date-picker",
            "display": false,
            "label": "CONFIG_START_DATE",
            "key": "startAtDate",
            "position": {
                "row": "8",
                "column": "0"
            },
        },
        {
            "type": "text",
            "display": false,
            "label": "CONFIG_START_TIME",
            "key": "startAtTime",
            "position": {
                "row": "8",
                "column": "0"
            },
        },
        {
            "type": "date-picker",
            "display": true,
            "label": "CONFIG_END_DATE",
            "key": "endAtDate",
            "position": {
                "row": "9",
                "column": "0"
            },
        },
        {
            "type": "text",
            "display": true,
            "label": "CONFIG_END_TIME",
            "key": "endAtTime",
            "position": {
                "row": "9",
                "column": "0"
            },
        },
        {
            "type": "radio",
            "display": true,
            "label": "HAS_SKU",
            "key": "hasSku",
            "position": {
                "row": "10",
                "column": "0"
            },
            "options": [
                { label: 'YES', value: "YES" },
                { label: 'NO', value: "NO" },
            ],
            required:true,
        },
        {
            "type": "text",
            "display": false,
            "label": "ENTER_INITIAL_ORDER_STORAGE",
            "key": "storageOrder",
            "position": {
                "row": "11",
                "column": "0"
            }
        },
        {
            "type": "text",
            "display": false,
            "label": "ENTER_ORDER_STORAGE_INCREASE_AMOUNT",
            "key": "storage_OrderIncreaseBy",
            "position": {
                "row": "11",
                "column": "1"
            }
        },
        {
            "type": "text",
            "display": false,
            "label": "ENTER_ORDER_STORAGE_DECREASE_AMOUNT",
            "key": "storage_OrderDecreaseBy",
            "position": {
                "row": "11",
                "column": "2"
            }
        },
        {
            "type": "text",
            "display": false,
            "label": "ENTER_INITIAL_ACTUAL_STORAGE",
            "key": "storageActual",
            "position": {
                "row": "12",
                "column": "0"
            }
        },
        {
            "type": "text",
            "display": false,
            "label": "ENTER_ACTUAL_STORAGE_INCREASE_AMOUNT",
            "key": "storage_ActualIncreaseBy",
            "position": {
                "row": "12",
                "column": "1"
            }
        },
        {
            "type": "text",
            "display": false,
            "label": "ENTER_ACTUAL_STORAGE_DECREASE_AMOUNT",
            "key": "storage_ActualDecreaseBy",
            "position": {
                "row": "12",
                "column": "2"
            }
        },
        {
            "type": "text",
            "display": false,
            "label": "ENTER_PRICE",
            "key": "price",
            "position": {
                "row": "13",
                "column": "0"
            },
        },
        {
            "type": "text",
            "display": false,
            "label": "ENTER_SALES",
            "key": "sales",
            "position": {
                "row": "14",
                "column": "0"
            },
        },
    ],
}


export const FORM_CONFIG_IMAGE: IForm = {
    "repeatable": true,
    "inputs": [
        {
            "type": "imageUpload",
            "display": true,
            "label": "ENTER_IMAGE_URL",
            "key": "imageUrl",
            "position": {
                "row": "0",
                "column": "0"
            }
        },
    ],
}

export const ATTR_SALE_FORM_CONFIG_IMAGE: IForm = {
    "repeatable": true,
    "inputs": [
        {
            "type": "select",
            "display": true,
            "label": "SELECT_AN_SALES_ATTRIBUTE",
            "key": "attributeId",
            "options":[],
            "position": {
                "row": "0",
                "column": "0"
            }
        },
        {
            "type": "select",
            "display": false,
            "label": "SELECT_AN_ATTRIBUTE_VALUE",
            "key": "attributeValueSelect",
            "options":[],
            "position": {
                "row": "0",
                "column": "1"
            }
        },
        {
            "type": "text",
            "display": false,
            "label": "ENTER_AN_ATTRIBUTE_VALUE",
            "key": "attributeValueManual",
            "position": {
                "row": "0",
                "column": "1"
            }
        },
        {
            "type": "form",
            "display": true,
            "label": "",
            "key": "imageChildForm",
            "form": {
                "repeatable": true,
                "inputs": [
                    {
                        "type": "imageUpload",
                        "display": true,
                        "label": "ENTER_IMAGE_URL",
                        "key": "imageUrl",
                        "position": {
                            "row": "0",
                            "column": "0"
                        }
                    },
                ]
            },
            "position": {
                "row": "1",
                "column": "0"
            }
        },
    ],
}

export const FORM_CONFIG_OPTIONS: IForm = {
    "repeatable": true,
    "inputs": [
        {
            "type": "text",
            "display": true,
            "label": "ENTER_PRODUCT_OPTION_TITLE",
            "key": "productOption",
            "position": {
                "row": "0",
                "column": "0"
            }
        },
        {
            "type": "form",
            "display": true,
            "label": "",
            "key": "optionForm",
            "form": {
                "repeatable": true,
                "inputs": [
                    {
                        "type": "text",
                        "display": true,
                        "label": "ENTER_OPTION_VALUE",
                        "key": "optionValue",
                        "position": {
                            "row": "0",
                            "column": "0"
                        }
                    },
                    {
                        "type": "text",
                        "display": true,
                        "label": "ENTER_OPTION_PRICE_CHANGE",
                        "key": "optionPriceChange",
                        "position": {
                            "row": "0",
                            "column": "1"
                        }
                    },
                ]
            },
            "position": {
                "row": "1",
                "column": "0"
            }
        },

    ],
}