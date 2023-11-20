import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { logout } from '../misc/utility';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
import { IAuthUser, IUpdatePwdCommand } from '../misc/interface';
import { DeviceService } from './device.service';
@Injectable({
  providedIn: 'root'
})
export class UserService extends EntityCommonService<IAuthUser, IAuthUser>{
  entityRepo: string = environment.serverUri + '/auth-svc/mgmt/users';
  constructor(private httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
    super(httpProxy, interceptor, deviceSvc);
  }
  revokeResourceOwnerToken(id: string): void {
    this.httpProxy.revokeUserToken(id).subscribe(result => {
      result ? this.interceptor.openSnackbar('OPERATION_SUCCESS_TOKEN') : this.interceptor.openSnackbar('OPERATION_FAILED');
    })
  }
  updateMyPwd(command: IUpdatePwdCommand, changeId: string): void {
    this.httpProxy.updateUserPwd(command, changeId).subscribe(result => {
      result ? this.interceptor.openSnackbar('OPERATION_SUCCESS_LOGIN') : this.interceptor.openSnackbar('OPERATION_FAILED');
      logout(undefined, this.httpProxy)
    });
  }
  batchUpdateUserStatus(ids: string[], status: 'LOCK' | 'UNLOCK', changeId: string) {
    this.httpProxy.batchUpdateUserStatus(this.entityRepo, ids, status, changeId).subscribe(result => {
      this.notify(result)
      this.deviceSvc.refreshSummary.next()
    })
  }
}
