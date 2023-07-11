import { Injectable } from '@angular/core';
import { TenantEntityService } from '../clazz/tenant-entity.service';
import { IProjectUser } from '../clazz/user.interface';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
import * as UUID from 'uuid/v1';
@Injectable({
  providedIn: 'root'
})
export class MyUserService extends TenantEntityService<IProjectUser, IProjectUser>{
  protected entityName: string = "users";
  constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
    super(httpProxy, interceptor, deviceSvc);
  }
  public addAdmin(userId: string) {
    const changeId = UUID();
    return this.httpProxySvc.addAdmin(this.projectId, userId, changeId)
  }
}
