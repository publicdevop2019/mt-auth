import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { logout } from '../clazz/utility';
import { IAuthUser, IResourceOwnerUpdatePwd } from '../clazz/validation/aggregate/user/interfaze-user';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
  providedIn: 'root'
})
export class UserService extends EntityCommonService<IAuthUser, IAuthUser>{
  entityRepo: string = environment.serverUri + '/auth-svc/mngmt/users';
  constructor(private httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
    super(httpProxy, interceptor,deviceSvc);
  }
  revokeResourceOwnerToken(id: string): void {
    this.httpProxy.revokeResourceOwnerToken(id).subscribe(result => {
      result ? this.interceptor.openSnackbar('OPERATION_SUCCESS_TOKEN') : this.interceptor.openSnackbar('OPERATION_FAILED');
    })
  }
  updateMyPwd(resourceOwner: IResourceOwnerUpdatePwd, changeId: string): void {
    this.httpProxy.updateResourceOwnerPwd(resourceOwner, changeId).subscribe(result => {
      result ? this.interceptor.openSnackbar('OPERATION_SUCCESS_LOGIN') : this.interceptor.openSnackbar('OPERATION_FAILED');
      /** clear authentication info */
      this.httpProxy.currentUserAuthInfo = undefined;
      logout()
    });
  }
  batchUpdateUserStatus(ids: string[], status: 'LOCK' | 'UNLOCK', changeId: string) {
    this.httpProxy.batchUpdateUserStatus(this.entityRepo, ids, status, changeId).subscribe(result => {
      this.notify(result)
      this.deviceSvc.refreshSummary.next()
    })
  }
}
