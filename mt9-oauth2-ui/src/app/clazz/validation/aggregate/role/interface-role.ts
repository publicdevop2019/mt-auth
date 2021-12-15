import { IIdBasedEntity } from "src/app/clazz/summary.component";

export interface IRole extends IIdBasedEntity{
    name:string
    description:string
    id:string;
    type:'CLIENT'|'USER'
}
export const TYPE_ROLE_ENUM=[
    { label: 'CLIENT', value: "CLIENT" },
    { label: 'USER', value: "USER" },
]