import { IIdName } from "mt-form-builder/lib/classes/template.interface";

export enum grantTypeEnums {
    refresh_token = 'REFRESH_TOKEN',
    password = 'PASSWORD',
    client_credentials = 'CLIENT_CREDENTIALS',
    authorization_code = 'AUTHORIZATION_CODE'
  }
  export enum scopeEnums {
    read = 'READ',
    write = 'WRITE',
    trust = 'TRUST'
  }
  export interface IClient extends IIdName{
    name: string;
    id: string;
    clientSecret?: string;
    description?: string;
    grantTypeEnums: grantTypeEnums[];
    grantedAuthorities: string[];
    scopeEnums: scopeEnums[];
    accessTokenValiditySeconds: number;
    refreshTokenValiditySeconds: number;
    resourceIds: string[]
    hasSecret: boolean;
    resourceIndicator: boolean;
    registeredRedirectUri: string[];
    autoApprove?: boolean;
    version:number;
  }