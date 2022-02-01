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
            },
            required:true,
        },
        {
            "type": "text",
            "display": true,
            "label": "NAME",
            "key": "name",
            "position": {
                "row": "0",
                "column": "0"
            },
            required:true,
        },
        {
            "type": "text",
            "display": false,
            "label": "PROJECT_ID",
            "key": "projectId",
            "position": {
                "row": "0",
                "column": "0"
            },
            required:true,
        },
        {
            "type": "paginated-select",
            "display": true,
            "label": "ENTER_PARENT_ID",
            "key": "parentId",
            "position": {
                "row": "3",
                "column": "0"
            },
            "options":[]
        },
    ],
}
