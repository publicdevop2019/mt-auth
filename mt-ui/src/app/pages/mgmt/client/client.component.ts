import { Component } from '@angular/core';
import { Observable, combineLatest } from 'rxjs';
import { take } from 'rxjs/operators';
import { CLIENT_TYPE, RESOURCE_NAME, grantTypeEnums } from 'src/app/misc/constant';
import { IClient, IOption } from 'src/app/misc/interface';
import { Utility } from 'src/app/misc/utility';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { FormGroup, FormControl } from '@angular/forms';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { DeviceService } from 'src/app/services/device.service';

@Component({
  selector: 'mgmt-app-client',
  templateUrl: './client.component.html',
  styleUrls: ['./client.component.css']
})
export class MgmtClientComponent {
  private url = Utility.getMgmtResource(RESOURCE_NAME.MGMT_CLIENTS)
  fg = new FormGroup({
    id: new FormControl({ value: '', disabled: true }),
    projectId: new FormControl(''),
    path: new FormControl(''),
    externalUrl: new FormControl(''),
    clientSecret: new FormControl(''),
    name: new FormControl(''),
    description: new FormControl(''),
    frontOrBackApp: new FormControl(''),
    grantType: new FormControl([]),
    registeredRedirectUri: new FormControl(''),
    refreshToken: new FormControl(''),
    resourceIndicator: new FormControl(''),
    autoApprove: new FormControl(''),
    accessTokenValiditySeconds: new FormControl(''),
    refreshTokenValiditySeconds: new FormControl(''),
    resourceId: new FormControl([]),
  });
  options: IOption[] = []
  constructor(
    public httpProxySvc: HttpProxyService,
    public router: RouterWrapperService,
    private deviceSvc: DeviceService
  ) {
    this.deviceSvc.updateDocTitle('MGMT_CLIENT_DOC_TITLE')
    this.fg.disable()
    this.doResume()
  }
  doResume(): void {
    const clientId = this.router.getMgmtClientIdFromUrl();
    this.httpProxySvc.readEntityById<IClient>(this.url, clientId).subscribe(next0 => {
      const var0: Observable<any>[] = [];
      if (next0.resourceIds && next0.resourceIds.length > 0) {
        var0.push(this.httpProxySvc.readEntityByQuery(this.url, 0, next0.resourceIds.length, 'id:' + next0.resourceIds.join('.')))
      }
      if (var0.length === 0) {
        this.resume(next0)
      } else {
        combineLatest(var0).pipe(take(1))
          .subscribe(next => {
            let count = -1;
            if (next0.resourceIds && next0.resourceIds.length > 0) {
              count++;
              this.options = next[count].data.map(e => <IOption>{ label: e.name, value: e.id })
            }
            this.resume(next0)
          })
      }
    })
  }
  private resume(next: IClient): void {
    const grantType = next.grantTypeEnums.filter(e => e !== grantTypeEnums.refresh_token);
    const value = {
      id: next.id,
      projectId: next.projectId,
      path: next.path ? next.path : '',
      externalUrl: next.externalUrl ? next.externalUrl : '',
      clientSecret: next.clientSecret,
      name: next.name,
      description: next.description || '',
      frontOrBackApp: next.types.filter(e => [CLIENT_TYPE.frontend_app, CLIENT_TYPE.backend_app].includes(e))[0],
      grantType: grantType,
      registeredRedirectUri: next.registeredRedirectUri ? next.registeredRedirectUri.join(',') : '',
      refreshToken: next.grantTypeEnums.find(e => e === grantTypeEnums.refresh_token),
      resourceIndicator: next.resourceIndicator,
      autoApprove: next.autoApprove,
      accessTokenValiditySeconds: next.accessTokenValiditySeconds,
      refreshTokenValiditySeconds: next.refreshTokenValiditySeconds,
      resourceId: next.resourceIds,
    }
    this.fg.patchValue(value)
  }
}
