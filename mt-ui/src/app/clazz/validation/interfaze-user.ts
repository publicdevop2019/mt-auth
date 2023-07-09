import { IIdBasedEntity } from "src/app/clazz/summary.component";

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
    email?: string;
    projectId: string;
    roles: string[];
    roleDetails?: { id: string, name: string }[];
    version: number;
}
export interface IProjectAdmin extends IIdBasedEntity{
    email: string;
    name: string;
}
export interface IPendingUser {
    email: string;
    password?: string;
    activationCode?: string;
    mobileNumber?: string;
    countryCode?: string;
}
export interface IForgetPasswordRequest {
    email: string;
    token?: string;
    newPassword?: string;
}
export interface IUpdatePwdCommand {
    password: string;
    currentPwd: string;
}