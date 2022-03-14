import { IIdBasedEntity } from "src/app/clazz/summary.component";

export interface IPermission extends IIdBasedEntity{
    name:string
    projectId:string
    parentId:string
    linkedApiIds:string[]
    systemCreate?:boolean
    linkedApiPermissionIds?:string[]
    type?:"COMMON"|'API'|'PROJECT'
}