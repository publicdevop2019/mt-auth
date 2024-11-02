import { grantTypeEnums, CLIENT_TYPE } from "./constant";
import { Observable } from "rxjs";
export interface IIdName {
    id: string;
    name: string;
    description?: string;
}
export interface ICacheProfile extends IIdBasedEntity {
    name: string;
    description: string;
    allowCache: boolean;
    cacheControl: string[];
    expires: number;
    maxAge: number;
    smaxAge: number;
    vary: string;
    etag: boolean;
    weakValidation: boolean;
}
export interface ICorsProfile extends IIdBasedEntity {
    name: string
    description: string
    id: string;
    allowCredentials: boolean;
    allowedHeaders: string[];
    allowOrigin: string[];
    exposedHeaders: string[];
    maxAge: number;
}
export interface IEndpointCreate {
    name: string;
    type: string;
}
export interface IEndpoint extends IIdBasedEntity {
    resourceId: string;
    resourceName?: string;
    description?: string;
    name: string;
    path: string;
    method?: string;
    websocket: boolean;
    secured: boolean;
    shared: boolean;
    external: boolean;
    csrfEnabled: boolean;
    corsProfileId?: string;
    projectId?: string;
    cacheProfileId?: string;
    expired?: boolean;
    expireReason?: string;
    replenishRate?: number,
    burstCapacity?: number,
}
export interface IProtectedEndpoint extends IIdBasedEntity {
    name: string;
    permissionId: string;
}
export interface IMgmtEndpoint extends IIdBasedEntity {
    resourceId: string;
    resourceName?: string;
    description?: string;
    name: string;
    path: string;
    method?: string;
    websocket: boolean;
    secured: boolean;
    shared: boolean;
    external: boolean;
    csrfEnabled: boolean;
    corsConfig?: {
        allowedHeaders: string[]
        credentials: boolean
        exposedHeaders: string[]
        maxAge: number
        origin: string[]
    };
    projectId?: string;
    cacheConfig?: {
        allowCache: boolean,
        cacheControl: string[],
        etag: boolean,
        expires: string,
        vary: string,
        maxAge: number,
        smaxAge: number,
        weakValidation: boolean
    };
    expired?: boolean;
    expireReason?: string;
    replenishRate?: number,
    burstCapacity?: number,
}
export interface IPermission extends IIdBasedEntity {
    name: string
    projectId: string
    description: string
    linkedApiIds: string[]
    systemCreate?: boolean
    linkedApiPermissionIds?: string[]
    linkedApiNames?: string[]
    type?: "COMMON" | 'API' | 'PROJECT'
}
export interface IRoleLinkedPermission {
    id: string
    name: string
    type?: "COMMON" | 'API' | 'PROJECT'
}
export interface IProjectSimple extends IIdBasedEntity {
    name: string
    createdBy?: string,
    createdAt?: string
    creatorName?: string
}
export interface IProjectDashboard extends IIdBasedEntity {
    name: string
    createdBy?: string,
    createdAt?: string
    creatorName?: string
    totalClient: number;
    totalEndpoint: number;
    totalUserOwned: number;
    totalPermissionCreated: number;
    totalRoleCreated: number;
}
export interface ILoginHistory {
    loginAt: number;
    ipAddress: string;
    agent: string;
}
export interface IAuthUser {
    id: string,
    email?: string;
    password?: string;
    locked: boolean;
    createdAt?: number;
    version: number;
    loginHistory?: ILoginHistory[]
}
export interface IProjectUser {
    id: string,
    displayName?: string;
    projectId: string;
    roles: string[];
    roleDetails?: { id: string, name: string }[];
    version: number;
}
export interface IProjectSimpleUser {
    id: string;
    email: string;
    mobile: string;
    username: string;
}
export interface IVerificationCodeRequest {
    countryCode?: string;
    mobileNumber?: string;
    email?: string;
}
export interface IForgetPasswordRequest {
    email?: string;
    mobileNumber?: string;
    countryCode?: string;
    token?: string;
    newPassword?: string;
}
export interface IUpdatePwdCommand {
    password: string;
    currentPwd: string;
}
export interface IClientCreate {
    name?: string;
    type?: string
}
export interface IClient extends IIdName {
    name: string;
    path?: string;
    externalUrl?: string;
    id: string;
    clientSecret?: string;
    projectId: string;
    description?: string;
    grantTypeEnums: grantTypeEnums[];
    types: CLIENT_TYPE[];
    accessTokenValiditySeconds?: number;
    refreshTokenValiditySeconds?: number;
    resourceIds?: string[]
    resources?: { name: string, id: string }[]
    hasSecret?: boolean;
    resourceIndicator?: boolean;
    registeredRedirectUri?: string[];
    autoApprove?: boolean;
    version: number;
}

export interface ITokenResponse {
    access_token: string;
    permissionIds: string[];
    tenantIds: string[];
    refresh_token?: string;
    token_type?: string;
    expires_in?: string;
    scope?: string;
    viewTenantId?: string;
}
export interface IAuthorizeParty {
    response_type: string;
    client_id: string;
    state: string;
    redirect_uri: string;
    projectId: string;
}
export interface IAuthorizeCode {
    authorize_code: string;
}
export interface IAutoApprove {
    autoApprove: boolean;
    id: string;
}
export interface IAuditable {
    modifiedAt: string;
    modifiedBy: string;
    createdAt: string;
    createdBy: string;
}
export interface IMfaResponse {
    message: string;
    partialMobile: string;
    partialEmail: string;
    deliveryMethod: boolean;
}
export interface ICheckSumResponse {
    hostValue: string
    proxyValue: { [key: string]: string }
}
export interface ICommonServerError {
    errorId: string;
    errors: string[]
}
export interface IOption {
    label: string;
    value: string;
}
export interface IQueryProvider {
    readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => Observable<ISumRep<IIdName>>;
}
export interface DialogData {
    data: { id: string, description: string }[]
}
export interface IRevokeToken {
    id: string;
    targetId: number;
    issuedAt: number;
    type: 'Client' | 'User';
    version: number;
}
export interface INotification extends IIdBasedEntity {
    title: string,
    descriptions: string[],
    date: number
    type: string
    status: string
}
export interface IIdBasedEntity {
    id: string;
    version: number
}
export interface ISumRep<T> {
    data: T[],
    totalItemCount: number
}
export interface IIdName extends IIdBasedEntity {
    name: string
}