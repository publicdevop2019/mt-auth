import { Injectable } from '@angular/core';
import { IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { TenantEntityService } from '../clazz/tenant-entity.service';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
import { ICacheProfile } from '../misc/interface';
@Injectable({
  providedIn: 'root'
})
export class MyCacheService extends TenantEntityService<ICacheProfile, ICacheProfile>  implements IQueryProvider {
  entityName: string = 'cache';
  constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
    super(httpProxy, interceptor,deviceSvc);
  }
  readByQuery (num: number, size: number, query?: string, by?: string, order?: string, header?: {}){
    return this.httpProxySvc.readEntityByQuery<ICacheProfile>(this.entityRepo, num, size,query, by, order, header)
  };
}
