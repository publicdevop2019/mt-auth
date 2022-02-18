import { IIdBasedEntity } from "src/app/clazz/summary.component";

export interface IPermission extends IIdBasedEntity{
    name:string
    projectId:string
    parentId:string
    linkedApiId:string
    systemCreate?:boolean
    linkedApiPermissionId?:string
    type?:"COMMON"|'API'|'PROJECT'
}