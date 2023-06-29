export interface ICheckSumResponse {
    hostValue: string
    proxyValue: { [key: string]: string }
}
export interface ICommonServerError {
    errorId: string;
    errors: string[]
}