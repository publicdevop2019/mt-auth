import { Injectable } from '@angular/core';
import { TenantEntityService } from '../clazz/tenant-entity.service';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
import { IProjectAdmin } from '../misc/interface';
@Injectable({
  providedIn: 'root'
})
export class MyAdminService extends TenantEntityService<IProjectAdmin, IProjectAdmin> {
  entityName: string = 'admins';
  constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
    super(httpProxy, interceptor, deviceSvc);
  }
}
