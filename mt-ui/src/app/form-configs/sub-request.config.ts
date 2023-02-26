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
            "label": "TARGET_PROJECT_NAME",
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
            "label": "REPLENISH_RATE",
            "key": "replenishRate",
            "position": {
                "row": "1",
                "column": "0"
            },
            required: true,
        },
        {
            "type": "text",
            "display": true,
            "label": "BURST_CAPACITY",
            "key": "burstCapacity",
            "position": {
                "row": "2",
                "column": "0"
            },
            required: true,
        },
    ],
}
