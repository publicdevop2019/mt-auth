import { Injectable } from '@angular/core';
import { TenantEntityService } from '../clazz/tenant-entity.service';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
import { IProjectUser } from '../misc/interface';
import { Utility } from '../misc/utility';
@Injectable({
  providedIn: 'root'
})
export class MyUserService extends TenantEntityService<IProjectUser, IProjectUser>{
  protected entityName: string = "users";
  constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
    super(httpProxy, interceptor, deviceSvc);
  }
  public addAdmin(userId: string) {
    const changeId = Utility.getChangeId();
    return this.httpProxySvc.addAdmin(this.projectId, userId, changeId)
  }
}
