import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IProjectSimple } from '../clazz/validation/aggregate/project/interface-project';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
  providedIn: 'root'
})
export class ProjectService extends EntityCommonService<IProjectSimple, IProjectSimple>{
  private PRODUCT_SVC_NAME = '/auth-svc';
  private ENTITY_NAME = '/mngmt/projects';
  public totalProjects: IProjectSimple[] = [];
  entityRepo: string = environment.serverUri + this.PRODUCT_SVC_NAME + this.ENTITY_NAME;
  constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
    super(httpProxy, interceptor, deviceSvc);
  }
  findTenantProjects(num: number, size: number) {
    return this.httpProxySvc.readEntityByQuery<IProjectSimple>(environment.serverUri + '/auth-svc/projects/tenant', num, size)
  };
  create(s: IProjectSimple, changeId: string) {
    return this.httpProxySvc.createEntity(environment.serverUri + '/auth-svc/projects', s, changeId)
  };
  getMyProject(projectId:string) {
    return this.httpProxySvc.readEntityById<IProjectSimple>(environment.serverUri + '/auth-svc/projects',projectId)
  };
  resolveNameById(id: string) {
    return this.totalProjects.find(e => e.id === id)?.name
  }
}
