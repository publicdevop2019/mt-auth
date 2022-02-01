import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IOrg } from '../pages/my-orgs/my-orgs.component';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
  providedIn: 'root'
})
export class OrgService extends EntityCommonService<IOrg, IOrg>{
  private ENTITY_NAME = '/auth-svc/organizations';
  entityRepo: string = environment.serverUri + this.ENTITY_NAME;
  role: string = '';
  constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
    super(httpProxy, interceptor,deviceSvc);
  }
  readByQuery(num: number, size: number, query?: string, by?: string, order?: string, header?: {}){
    return this.httpProxySvc.readEntityByQuery<IOrg>(this.entityRepo, this.role, num, size,query, by, order, header)
 }
}
