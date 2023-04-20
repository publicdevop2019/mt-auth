import { Injectable } from '@angular/core';
import { IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { TenantEntityService } from '../clazz/tenant-entity.service';
import { ICorsProfile } from '../clazz/validation/aggregate/cors/interface-cors';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
  providedIn: 'root'
})
export class MyCorsProfileService extends TenantEntityService<ICorsProfile, ICorsProfile>  implements IQueryProvider {
  entityName: string = 'cors';
  constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
    super(httpProxy, interceptor,deviceSvc);
  }
  readByQuery(num: number, size: number, query?: string, by?: string, order?: string, header?: {}){
     return this.httpProxySvc.readEntityByQuery<ICorsProfile>(this.entityRepo, num, size,query, by, order, header)
  }
}
