import { Component } from '@angular/core';
import { RESOURCE_NAME, grantTypeEnums } from 'src/app/misc/constant';
import { IClient } from 'src/app/misc/interface';
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
    clientSecret: new FormControl(''),
    name: new FormControl(''),
    description: new FormControl(''),
    frontOrBackApp: new FormControl(''),
    grantType: new FormControl([]),
    registeredRedirectUri: new FormControl(''),
    refreshToken: new FormControl(''),
    accessTokenValiditySeconds: new FormControl(''),
    refreshTokenValiditySeconds: new FormControl(''),
    resourceId: new FormControl([]),
  });
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
      this.resume(next0)
    })
  }
  private resume(next: IClient): void {
    const grantType = next.grantTypeEnums.filter(e => e !== grantTypeEnums.refresh_token);
    const value = {
      id: next.id,
      projectId: next.projectId,
      clientSecret: next.clientSecret,
      name: next.name,
      description: next.description || '',
      frontOrBackApp: next.type,
      grantType: grantType,
      registeredRedirectUri: next.registeredRedirectUri ? next.registeredRedirectUri.join(',') : '',
      refreshToken: next.grantTypeEnums.find(e => e === grantTypeEnums.refresh_token),
      accessTokenValiditySeconds: next.accessTokenValiditySeconds,
      refreshTokenValiditySeconds: next.refreshTokenValiditySeconds,
    }
    this.fg.patchValue(value)
  }
}
