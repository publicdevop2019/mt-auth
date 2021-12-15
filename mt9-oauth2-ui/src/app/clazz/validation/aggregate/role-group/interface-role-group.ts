import { IIdBasedEntity } from "src/app/clazz/summary.component";

export interface IRoleGroup extends IIdBasedEntity{
    name:string
    description:string
    groupType:'USER'|'CLIENT'|'BOTH'
    clientRoles?:string[]
    userRoles?:string[]
}