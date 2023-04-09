
export interface ITokenResponse {
  access_token: string;
  permissionIds: string[];
  tenantIds: string[];
  refresh_token?: string;
  token_type?: string;
  expires_in?: string;
  scope?: string;
}
export interface IAuthorizeParty {
  response_type: string;
  client_id: string;
  state: string;
  redirect_uri: string;
  projectId: string;
}
export interface IAuthorizeCode {
  authorize_code: string;
}
export interface IAutoApprove {
  autoApprove: boolean;
  id: string;
}
export interface IAuditable {
  modifiedAt: string;
  modifiedBy: string;
  createdAt: string;
  createdBy: string;
}
export interface IMfaResponse {
  mfaId: string;
  message: string;
}