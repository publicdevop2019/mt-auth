import { IForm } from 'mt-form-builder/lib/classes/template.interface';
import { TYPE_ROLE_ENUM } from '../clazz/validation/aggregate/role/interface-role';

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
            "type": "select",
            "display": true,
            "label": "PLEASE_SELECT_ROLE_TYPE",
            "key": "type",
            "position": {
                "row": "3",
                "column": "0"
            },
            "options": TYPE_ROLE_ENUM,
        },
    ],
}
