import { IIdBasedEntity } from "src/app/clazz/summary.component";

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