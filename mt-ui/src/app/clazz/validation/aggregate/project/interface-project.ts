import { IIdName } from "mt-form-builder/lib/classes/template.interface";
import { IIdBasedEntity } from "src/app/clazz/summary.component";

export interface IProject extends IIdBasedEntity{
    name:string
}
export interface IProjectSimple extends IIdBasedEntity{
    name:string
}
