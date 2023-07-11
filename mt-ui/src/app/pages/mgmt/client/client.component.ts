import { Component, Inject, OnDestroy } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { CLIENT_TYPE, grantTypeEnums, IClient } from 'src/app/clazz/client.interface';
import { FORM_CONFIG } from 'src/app/form-configs/client.config';
import { MgmtClientService } from 'src/app/services/mgmt-client.service';

@Component({
  selector: 'mgmt-app-client',
  templateUrl: './client.component.html',
  styleUrls: ['./client.component.css']
})
export class MgmtClientComponent implements OnDestroy {
  public formId = 'mgmtClient';
  constructor(
    public clientSvc: MgmtClientService,
    public fis: FormInfoService,
    public bottomSheetRef: MatBottomSheetRef<MgmtClientComponent>,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: IBottomSheet<IClient>
  ) {
    const client = data.from
    this.fis.init(FORM_CONFIG, this.formId)
    if (this.data.context === 'edit') {
      const showArray: string[] = [];
      const hideArray: string[] = [];
      if ((client.types).includes(CLIENT_TYPE.backend_app)) {
        showArray.push('clientSecret')
        showArray.push('path')
        showArray.push('resourceIndicator')
        showArray.push('resourceId')
      } else {
        hideArray.push('clientSecret')
        hideArray.push('path')
        hideArray.push('resourceIndicator')
        hideArray.push('resourceId')
      }
      if ((client.grantTypeEnums as string[] || []).includes('AUTHORIZATION_CODE')) {
        showArray.push('registeredRedirectUri')
        showArray.push('autoApprove')
      } else {
        hideArray.push('registeredRedirectUri')
        hideArray.push('autoApprove')
      }
      if ((client.grantTypeEnums as string[] || []).includes('PASSWORD')) {
        showArray.push('refreshToken')
        showArray.push('refreshTokenValiditySeconds')
      } else {
        hideArray.push('refreshToken')
        hideArray.push('refreshTokenValiditySeconds')
      }
      this.fis.showIfMatch(this.formId, showArray)
      this.fis.hideIfMatch(this.formId, hideArray)
      this.fis.disableForm(this.formId)
      const var0: Observable<any>[] = [];
      if (client.resourceIds && client.resourceIds.length > 0) {
        this.clientSvc.readEntityByQuery(0, client.resourceIds.length, 'id:' + client.resourceIds.join('.'))
          .pipe(take(1))
          .subscribe(next => {
            this.fis.updateOption(this.formId, 'resourceId', next.data.map(e => <IOption>{ label: e.name, value: e.id }))
            this.resume()
          })
      }else{
        this.resume()
      }
    };
  }
  resume(): void {
    const client = this.data.from
    const grantType: string = client.grantTypeEnums.filter(e => e !== grantTypeEnums.refresh_token)[0];
    this.fis.formGroups[this.formId].patchValue({
      id: client.id,
      hasSecret: client.hasSecret,
      projectId: client.projectId,
      path: client.path ? client.path : '',
      clientSecret: client.hasSecret ? '*****' : '',
      name: client.name,
      description: client.description || '',
      frontOrBackApp: client.types.filter(e => [CLIENT_TYPE.frontend_app, CLIENT_TYPE.backend_app].includes(e))[0],
      grantType: grantType,
      registeredRedirectUri: client.registeredRedirectUri ? client.registeredRedirectUri.join(',') : '',
      refreshToken: grantType === 'PASSWORD' ? client.grantTypeEnums.some(e => e === grantTypeEnums.refresh_token) : false,
      resourceIndicator: client.resourceIndicator,
      autoApprove: client.autoApprove,
      accessTokenValiditySeconds: client.accessTokenValiditySeconds,
      refreshTokenValiditySeconds: client.refreshTokenValiditySeconds,
      resourceId: client.resourceIds,
    });
  }
  ngOnDestroy(): void {
    this.fis.reset(this.formId);
  }
  dismiss(event: MouseEvent) {
    this.bottomSheetRef.dismiss();
    event.preventDefault();
  }
}
