import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
export interface IRevokeToken {
  id: string;
  targetId:number;
  issuedAt:number;
  type:'Client'|'User';
  version:number;
}
@Injectable({
  providedIn: 'root'
})
export class RevokeTokenService extends EntityCommonService<IRevokeToken, IRevokeToken>{
  private AUTH_SVC_NAME = '/auth-svc';
  private ENTITY_NAME = '/revoke-tokens';
  entityRepo: string = environment.serverUri + this.AUTH_SVC_NAME + this.ENTITY_NAME;
  role: string = '';
  constructor(private httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
    super(httpProxy, interceptor,deviceSvc);
  }
}
