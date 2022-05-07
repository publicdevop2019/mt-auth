import { IIdBasedEntity } from "src/app/clazz/summary.component";

export interface IProjectSimple extends IIdBasedEntity{
    name:string
    createdBy?:string,
    createdAt?:string
    creatorName?:string
}
