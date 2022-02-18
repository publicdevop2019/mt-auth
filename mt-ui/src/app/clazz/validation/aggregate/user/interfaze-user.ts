export interface IAuthUser {
    id: string,
    email?: string;
    password?: string;
    locked: boolean;
    createdAt?:number;
    version:number;
}
export interface IProjectUser {
    id: string,
    email?: string;
    projectId: string;
    roles: string[];
    version:number;
}
export interface IPendingResourceOwner {
    email: string;
    password?: string;
    activationCode?: string;
}
export interface IForgetPasswordRequest {
    email: string;
    token?: string;
    newPassword?: string;
}
export interface IResourceOwnerUpdatePwd {
    password: string;
    currentPwd: string;
}