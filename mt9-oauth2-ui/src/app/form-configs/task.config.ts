import { IForm } from 'mt-form-builder/lib/classes/template.interface';
export const FORM_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "select",
            "display": true,
            "label": "PLEASE_SELECT_TASK_NAME",
            "key": "taskName",
            "position": {
                "row": "0",
                "column": "0"
            },
            "options": [
                { label: 'CREATE_ORDER_DTX', value: "/createOrderDtx" },
                { label: 'CANCEL_CREATE_ORDER_DTX', value: "/cancelCreateOrderDtx" },
                { label: 'RESERVE_ORDER_DTX', value: "/reserveOrderDtx" },
                { label: 'CANCEL_RESERVE_ORDER_DTX', value: "/cancelReserveOrderDtx" },
                { label: 'RECYCLE_ORDER_DTX', value: "/recycleOrderDtx" },
                { label: 'CANCEL_RECYCLE_ORDER_DTX', value: "/cancelRecycleOrderDtx" },
                { label: 'CONFIRM_PAYMENT_DTX', value: "/confirmOrderPaymentDtx" },
                { label: 'CANCEL_CONFIRM_PAYMENT_DTX', value: "/cancelConfirmOrderPaymentDtx" },
                { label: 'CONCLUDE_ORDER_DTX', value: "/concludeOrderDtx" },
                { label: 'CANCEL_CONCLUDE_ORDER_DTX', value: "/cancelConcludeOrderDtx" },
                { label: 'UPDATE_ORDER_ADDRESS_DTX', value: "/updateOrderAddressDtx" },
                { label: 'CANCEL_UPDATE_ORDER_ADDRESS_DTX', value: "/cancelUpdateOrderAddressDtx" },
                { label: 'INVALID_ORDER_DTX', value: "/invalidOrderDtx" },
                { label: 'CANCEL_INVALID_ORDER_DTX', value: "/cancelInvalidOrderDtx" },
            ],
        },
    ],
}
