import { Injectable } from '@angular/core';
import { TenantEntityService } from '../clazz/tenant-entity.service';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
import { IClient } from '../misc/interface';
@Injectable({
  providedIn: 'root'
})
export class MyClientService extends TenantEntityService<IClient, IClient> {
  entityName: string = 'clients';
  constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
    super(httpProxy, interceptor, deviceSvc);
  }
  getDropdownClients(num: number, size: number, query?: string, by?: string, order?: string, headers?: {}) {
    return this.httpProxySvc.readEntityByQuery<IClient>(this.entityRepo+'/dropdown', num, size, query, by, order, headers)
  };
}
