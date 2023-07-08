import { IForm } from 'mt-form-builder/lib/classes/template.interface';

export const FORM_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "text",
            "display": false,
            "label": "ID",
            "key": "id",
            required:true,
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
            "display": false,
            "label": "PROJECT_ID",
            "key": "projectId",
            required:true,
        },
        {
            "type": "paginated-select",
            "display": true,
            "label": "ENTER_PARENT_ID",
            "key": "parentId",
            "options":[]
        },
        {
            "type": "checkbox",
            "display": true,
            "label": "",
            "key": "linkApi",
            "options":[{
                label:'LINK_TO_API',value:'linkToApi'
            }]
        },
        {
            "type": "paginated-select",
            "display": false,
            "multiple": true,
            "label": "SELECT_API",
            "key": "apiId",
            "options":[]
        },
    ],
}
