import { IIdBasedEntity } from "src/app/clazz/summary.component";

export interface ICacheProfile extends IIdBasedEntity {
  name: string;
  description: string;
  allowCache:boolean;
  cacheControl: string[];
  expires: number;
  maxAge: number;
  smaxAge: number;
  vary: string;
  etag: boolean;
  weakValidation: boolean;
}