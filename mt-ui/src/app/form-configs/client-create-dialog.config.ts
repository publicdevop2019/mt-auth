import { IForm } from 'mt-form-builder/lib/classes/template.interface';
import { GRANT_TYPE_LIST } from '../misc/constant';

export const FORM_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "text",
            "display": false,
            "label": "",
            "key": "projectId",
            "position": {
                "row": "0",
                "column": "0"
            }
        },
        {
            "type": "text",
            "display": true,
            "label": "ENTER_CLIENT_NAME",
            "key": "name",
            required: true,
            "position": {
                "row": "1",
                "column": "0"
            }
        },
        {
            "type": "radio",
            "display": true,
            "label": "SELECT_APP_TYPE_1",
            "key": "frontOrBackApp",
            direction:'column',
            "position": {
                "row": "5",
                "column": "0"
            },
            "options": [
                { label: 'TYPE_MIXED_APP', value: 'BACKEND_APP'},
                { label: 'TYPE_FRONTEND_APP', value: 'FRONTEND_APP'},
            ],
            required: true,
        },
    ],
}