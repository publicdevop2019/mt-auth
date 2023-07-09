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
            "label": "NAME",
            "key": "name",
            required: true,
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
            "type": "paginated-select",
            "display": true,
            "label": "PARENT_ID",
            "key": "parentId",
            options:[],
            "position": {
                "row": "3",
                "column": "0"
            },
        },
        {
            "type": "text",
            "display": false,
            "label": "",
            "key": "projectId",
            "position": {
                "row": "4",
                "column": "0"
            },
        },
    ],
}
export const FORM_CONFIG_SHARED: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "paginated-select",
            "display": true,
            "label": "EXTERNAL_PERMISSION",
            "key": "sharedApi",
            multiple:true,
            "position": {
                "row": "0",
                "column": "0"
            },
            options:[],
        },
    ],
}