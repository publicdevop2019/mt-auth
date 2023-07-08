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
            required: true,
        },
        {
            "type": "text",
            "display": true,
            "label": "DESCRIPTION",
            "key": "description",
        },
        {
            "type": "paginated-select",
            "display": true,
            "label": "PARENT_ID",
            "key": "parentId",
            options:[],
        },
        {
            "type": "text",
            "display": false,
            "label": "",
            "key": "projectId",
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
            options:[],
        },
    ],
}
