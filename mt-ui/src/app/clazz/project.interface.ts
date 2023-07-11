import { IIdBasedEntity } from "src/app/clazz/summary.component";

export interface IProjectSimple extends IIdBasedEntity{
    name:string
    createdBy?:string,
    createdAt?:string
    creatorName?:string
}
export interface IProjectDashboard extends IIdBasedEntity{
    name:string
    createdBy?:string,
    createdAt?:string
    creatorName?:string
    totalClient: number;
    totalEndpoint: number;
    totalUserOwned: number;
    totalPermissionCreated: number;
    totalRoleCreated: number;
}
