import { IProductOptions } from './aggregate/product/interfaze-product';

export interface ITokenResponse {
  access_token: string;
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
export interface IOrder extends IAuditable {
  id: string;
  productList: ICartItem[];
  address: IAddress;
  paymentType: string;
  paymentAmt: string;
  orderState: string;
  modifiedByUserAt: string;
  paid: boolean;
  version:number;
}
export interface IAuditable {
  modifiedAt: string;
  modifiedBy: string;
  createdAt: string;
  createdBy: string;
}
export interface ICartItem {
  id: string;
  finalPrice: string;
  selectedOptions: IProductOptions[];
  attributesSales: string[]
  attrIdMap: { [key: number]: string }
  imageUrlSmall: string;
  productId: number;
  name: string;
}
export interface IAddress {
  id: string;
  country: string;
  province: string;
  postalCode: string;
  fullName: string;
  line1: string;
  line2: string;
  city: string;
  phoneNumber: string;
}
export interface IPayment {
  id: string;
  type: string;
  accountNumber: string;
  accountHolderName: string;
  expireDate: string;
  cvv?: string;
}