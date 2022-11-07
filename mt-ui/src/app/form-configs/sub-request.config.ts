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
            "type": "paginated-select",
            "display": true,
            "label": "PROJECT_NAME",
            "key": "projectId",
            "position": {
                "row": "0",
                "column": "0"
            },
            options:[],
            required: true,
        },
        {
            "type": "text",
            "display": true,
            "label": "MAX_INVOKE_PER_SEC",
            "key": "maxInvokePerSec",
            "position": {
                "row": "1",
                "column": "0"
            },
            required: true,
        },
        {
            "type": "text",
            "display": true,
            "label": "MAX_INVOKE_PER_MIN",
            "key": "maxInvokePerMin",
            "position": {
                "row": "2",
                "column": "0"
            },
            required: true,
        },
    ],
}
