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
            "type": "paginated-select",
            "display": true,
            "label": "TARGET_PROJECT_NAME",
            "key": "projectId",
            options:[],
            required: true,
        },
        {
            "type": "text",
            "display": true,
            "label": "REPLENISH_RATE",
            "key": "replenishRate",
            required: true,
        },
        {
            "type": "text",
            "display": true,
            "label": "BURST_CAPACITY",
            "key": "burstCapacity",
            required: true,
        },
    ],
}
