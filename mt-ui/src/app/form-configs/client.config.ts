import { IForm } from 'mt-form-builder/lib/classes/template.interface';
import { CLIENT_TYPE } from '../clazz/validation/aggregate/client/interfaze-client';
import { GRANT_TYPE_LIST } from '../clazz/validation/constant';

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
            "label": "ENTER_NAME",
            "key": "name",
            required: true,
            "position": {
                "row": "1",
                "column": "0"
            }
        },
        {
            "type": "text",
            "display": true,
            "label": "ENTER_DESCRIPTION",
            "key": "description",
            "position": {
                "row": "2",
                "column": "0"
            },
        },
        {
            "type": "text",
            "display": true,
            "label": "ENTER_PATH",
            "key": "path",
            "position": {
                "row": "3",
                "column": "0"
            },
        },
        {
            "type": "checkbox",
            "display": true,
            "label": "",
            "key": "hasSecret",
            "position": {
                "row": "4",
                "column": "0"
            },
            "options": [
                { label: 'HAS_SECRET', value: "Has secret" }
            ],
        },
        {
            "type": "text",
            "display": false,
            "label": "ENTER_CLIENT_SECRET",
            "key": "clientSecret",
            "position": {
                "row": "5",
                "column": "0"
            },
            required: true,
        },
        {
            "type": "select",
            "display": true,
            "label": "SELECT_A_GRANT_TYPE",
            "key": "grantType",
            "position": {
                "row": "6",
                "column": "0"
            },
            "options": GRANT_TYPE_LIST,
            required: true,
        },
        {
            "type": "checkbox",
            "display": true,
            "label": "",
            "key": "isRoot",
            "position": {
                "row": "7",
                "column": "0"
            },
            "options": [{
                label: 'IS_ROOT_APPLICATION', value: 'isRootApplication'
            }],
            required: true,
        },
        {
            "type": "radio",
            "display": true,
            "label": "SELECT_APP_TYPE_1",
            "key": "firstOrThirdApp",
            direction:'row',
            "position": {
                "row": "8",
                "column": "0"
            },
            "options": [
                { label: 'FIRST_PARTY', value: 'FIRST_PARTY'},
                { label: 'THIRD_PARTY', value: 'THIRD_PARTY'},
            ],
            required: true,
        },
        {
            "type": "radio",
            "display": true,
            "label": "SELECT_APP_TYPE_2",
            "key": "frontOrBackApp",
            direction:'row',
            "position": {
                "row": "9",
                "column": "0"
            },
            "options": [
                { label: 'FRONTEND_APP', value: 'FRONTEND_APP'},
                { label: 'BACKEND_APP', value: 'BACKEND_APP'},
            ],
            required: true,
        },
        {
            "type": "text",
            "display": false,
            "label": "ENTER_REDIRECT_URI",
            "key": "registeredRedirectUri",
            "position": {
                "row": "10",
                "column": "0"
            },
            required: true,
        },
        {
            "type": "checkbox",
            "display": false,
            "label": "",
            "key": "refreshToken",
            "position": {
                "row": "11",
                "column": "0"
            },
            "options": [
                { label: 'ADD_REFRESH_TOKEN', value: "Add Refresh Token" },
            ]
        },
        {
            "type": "checkbox",
            "display": true,
            "label": "",
            "key": "resourceIndicator",
            "position": {
                "row": "12",
                "column": "0"
            },
            "options": [
                { label: 'SET_AS_RESOURCE', value: "Set As Resource" },
            ],
        },
        {
            "type": "checkbox",
            "display": false,
            "label": "",
            "key": "autoApprove",
            "position": {
                "row": "13",
                "column": "0"
            },
            "options": [
                { label: 'AUTO_APPROVE_AUTHORIZE_REQUEST', value: "Auto approve authorize request" },
            ],
        },
        {
            "type": "paginated-select",
            "display": true,
            "multiple": true,
            "label": "PLEASE_SELECT_RESOUCE_ID(S)",
            "key": "resourceId",
            "position": {
                "row": "14",
                "column": "0"
            },
            "options": [
            ],
        },
        {
            "type": "text",
            "display": true,
            "label": "ACCESS_TOKEN_VALIDITY_SECONDS",
            "key": "accessTokenValiditySeconds",
            "position": {
                "row": "15",
                "column": "0"
            },
            required: true,
        },
        {
            "type": "text",
            "display": false,
            "label": "REFRESH_TOKEN_VALIDITY_SECONDS",
            "key": "refreshTokenValiditySeconds",
            "position": {
                "row": "16",
                "column": "0"
            },
            required: true,
        },
    ],
}
