import { Injectable } from '@angular/core';
import { IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { ICorsProfile } from '../clazz/validation/aggregate/cors/interface-cors';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
  providedIn: 'root'
})
export class CORSProfileService extends EntityCommonService<ICorsProfile, ICorsProfile>  implements IQueryProvider {
  private ENTITY_NAME = '/auth-svc/cors';
  entityRepo: string = environment.serverUri + this.ENTITY_NAME;
  constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
    super(httpProxy, interceptor,deviceSvc);
  }
  readByQuery(num: number, size: number, query?: string, by?: string, order?: string, header?: {}){
     return this.httpProxySvc.readEntityByQuery<ICorsProfile>(this.entityRepo, num, size,query, by, order, header)
  }
}
