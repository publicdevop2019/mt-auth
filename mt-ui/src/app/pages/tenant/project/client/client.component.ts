import { Component, Inject, OnDestroy } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IOption, IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { CLIENT_TYPE, grantTypeEnums, IClient } from 'src/app/clazz/client.interface';
import { GRANT_TYPE_LIST } from 'src/app/clazz/constant';
import { hasValue } from 'src/app/clazz/validator-common';
import { FORM_CONFIG } from 'src/app/form-configs/client.config';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyClientService } from 'src/app/services/my-client.service';
import { Logger, Utility } from 'src/app/clazz/utility';
import { Validator } from 'src/app/clazz/validator-next-common';

@Component({
  selector: 'app-client',
  templateUrl: './client.component.html',
  styleUrls: ['./client.component.css']
})
export class ClientComponent implements OnDestroy {
  formId: string;
  allowError: boolean = false;
  changeId: string = Utility.getChangeId();
  constructor(
    public clientSvc: MyClientService,
    public httpProxySvc: HttpProxyService,
    public fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: IBottomSheet<IClient>,
    public bottomSheetRef: MatBottomSheetRef<ClientComponent>,
  ) {
    clientSvc.setProjectId(this.data.params['projectId'])
    this.fis.queryProvider[this.formId + '_' + 'resourceId'] = this.getResourceIds();
    this.fis.init(FORM_CONFIG, this.formId)
    if (this.data.context === 'new') {
      this.fis.formGroups[this.formId].get('projectId').setValue(this.data.params['projectId'])
      this.fis.formGroups[this.formId].valueChanges.subscribe(() => {
        if (this.allowError) {
          this.validateCreateForm()
        }
      })
    }
    if (this.data.context === 'edit') {
      this.fis.formGroups[this.formId].valueChanges.subscribe(() => {
        if (this.allowError) {
          this.validateUpdateForm()
        }
      })
    }
    this.fis.formGroups[this.formId].get('frontOrBackApp').valueChanges.subscribe((next) => {
      this.fis.resetValue(this.formId, 'grantType')
      this.fis.resetValue(this.formId, 'resourceId')
      this.fis.resetValue(this.formId, 'accessTokenValiditySeconds')
      this.fis.resetValue(this.formId, 'refreshToken')
      this.fis.resetValue(this.formId, 'refreshTokenValiditySeconds')
      const var0 = ['clientSecret', 'path', 'externalUrl', 'resourceIndicator']
      const var1 = ['registeredRedirectUri', 'accessTokenValiditySeconds', 'refreshToken', 'refreshTokenValiditySeconds', 'autoApprove']
      if (next === 'BACKEND_APP') {
        this.fis.showIfMatch(this.formId, var0)
      } else {
        this.fis.hideIfMatch(this.formId, var0)
      }
      this.fis.hideIfMatch(this.formId, var1)
      this.fis.showIfMatch(this.formId, ['grantType', 'resourceId'])
      if (next === 'FRONTEND_APP') {
        this.fis.updateOption(this.formId, 'grantType', GRANT_TYPE_LIST)
      } else {
        this.fis.updateOption(this.formId, 'grantType', GRANT_TYPE_LIST.filter(e => e.value !== 'AUTHORIZATION_CODE'))
      }
    })
    this.fis.formGroups[this.formId].get('grantType').valueChanges.subscribe((next) => {
      const var0 = ['accessTokenValiditySeconds']
      if (next) {
        this.fis.showIfMatch(this.formId, var0)
      } else {
        this.fis.hideIfMatch(this.formId, var0)
      }
      if ((next as string[]).includes('PASSWORD')) {
        this.fis.showIfMatch(this.formId, ['refreshToken'])
      } else {
        const var1 = ['refreshToken', 'refreshTokenValiditySeconds']
        this.fis.hideIfMatch(this.formId, var1)
      }
      const var2 = ['registeredRedirectUri', 'autoApprove']
      if ((next as string[]).includes('AUTHORIZATION_CODE')) {
        this.fis.showIfMatch(this.formId, var2)
      } else {
        this.fis.hideIfMatch(this.formId, var2)
      }
    })
    this.fis.formGroups[this.formId].get('refreshToken').valueChanges.subscribe((next) => {
      const var3 = ['refreshTokenValiditySeconds']
      if (next) {
        this.fis.showIfMatch(this.formId, var3)
      } else {
        this.fis.hideIfMatch(this.formId, var3)
      }
    })
    if (this.data.context === 'edit') {
      const var0: Observable<any>[] = [];
      if (this.data.from.resourceIds && this.data.from.resourceIds.length > 0) {
        var0.push(this.clientSvc.readEntityByQuery(0, this.data.from.resourceIds.length, 'id:' + this.data.from.resourceIds.join('.')))
      }
      if (var0.length === 0) {
        this.resume()
      } else {
        combineLatest(var0).pipe(take(1))
          .subscribe(next => {
            let count = -1;
            if (this.data.from.resourceIds && this.data.from.resourceIds.length > 0) {
              count++;
              const nextOptions = next[count].data.map(e => <IOption>{ label: e.name, value: e.id })
              this.fis.updateOption(this.formId, 'resourceId', nextOptions)
            }
            this.resume()
          })

      }
    };
  }
  resume(): void {
    const grantType: string = this.data.from.grantTypeEnums.filter(e => e !== grantTypeEnums.refresh_token)[0];
    this.fis.formGroups[this.formId].patchValue({
      id: this.data.from.id,
      projectId: this.data.from.projectId,
      path: this.data.from.path ? this.data.from.path : '',
      externalUrl: this.data.from.externalUrl ? this.data.from.externalUrl : '',
      clientSecret: this.data.from.hasSecret ? '*****' : '',
      name: this.data.from.name,
      description: this.data.from.description || '',
      frontOrBackApp: this.data.from.types.filter(e => [CLIENT_TYPE.frontend_app, CLIENT_TYPE.backend_app].includes(e))[0],
      grantType: grantType,
      registeredRedirectUri: this.data.from.registeredRedirectUri ? this.data.from.registeredRedirectUri.join(',') : '',
      refreshToken: grantType === 'PASSWORD' ? this.data.from.grantTypeEnums.some(e => e === grantTypeEnums.refresh_token) : false,
      resourceIndicator: this.data.from.resourceIndicator,
      autoApprove: this.data.from.autoApprove,
      accessTokenValiditySeconds: this.data.from.accessTokenValiditySeconds,
      refreshTokenValiditySeconds: this.data.from.refreshTokenValiditySeconds,
      resourceId: this.data.from.resourceIds,
    });
  }
  getResourceIds() {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.httpProxySvc.readEntityByQuery<IClient>(this.clientSvc.entityRepo, num, size, `resourceIndicator:1`, by, order, header)
      }
    } as IQueryProvider
  }
  ngOnDestroy(): void {
    this.fis.reset(this.formId);
  }
  convertToPayload(): IClient {
    let formGroup = this.fis.formGroups[this.formId];
    let grants: grantTypeEnums[] = [];
    const types: CLIENT_TYPE[] = [];
    if (formGroup.get('grantType').value as grantTypeEnums) {
      grants.push(formGroup.get('grantType').value as grantTypeEnums);
    }
    if (formGroup.get('refreshToken').value)
      grants.push(grantTypeEnums.refresh_token);
    types.push(formGroup.get('frontOrBackApp').value);
    if (types.includes(CLIENT_TYPE.frontend_app)) {
      return {
        id: formGroup.get('id').value,
        name: formGroup.get('name').value,
        description: formGroup.get('description').value ? formGroup.get('description').value : null,
        grantTypeEnums: grants,
        types: types,
        accessTokenValiditySeconds: +formGroup.get('accessTokenValiditySeconds').value,
        refreshTokenValiditySeconds: formGroup.get('refreshToken').value ? (hasValue(formGroup.get('refreshTokenValiditySeconds').value) ? +formGroup.get('refreshTokenValiditySeconds').value : null) : null,
        resourceIds: formGroup.get('resourceId').value ? formGroup.get('resourceId').value as string[] : [],
        registeredRedirectUri: formGroup.get('registeredRedirectUri').value ? (formGroup.get('registeredRedirectUri').value as string).split(',') : null,
        autoApprove: formGroup.get('grantType').value === grantTypeEnums.authorization_code ? !!formGroup.get('autoApprove').value : null,
        version: this.data.from && this.data.from.version,
        projectId: formGroup.get('projectId').value
      }
    }
    return {
      id: formGroup.get('id').value,
      name: formGroup.get('name').value,
      path: formGroup.get('path').value ? formGroup.get('path').value : undefined,
      externalUrl: formGroup.get('externalUrl').value ? formGroup.get('externalUrl').value : undefined,
      description: formGroup.get('description').value ? formGroup.get('description').value : null,
      hasSecret: formGroup.get('clientSecret').value === '*****',
      clientSecret: formGroup.get('clientSecret').value === '*****' ? null : formGroup.get('clientSecret').value || '',
      grantTypeEnums: grants,
      types: types,
      accessTokenValiditySeconds: +formGroup.get('accessTokenValiditySeconds').value,
      refreshTokenValiditySeconds: formGroup.get('refreshToken').value ? (hasValue(formGroup.get('refreshTokenValiditySeconds').value) ? +formGroup.get('refreshTokenValiditySeconds').value : null) : null,
      resourceIndicator: !!formGroup.get('resourceIndicator').value,
      resourceIds: formGroup.get('resourceId').value ? formGroup.get('resourceId').value as string[] : [],
      registeredRedirectUri: formGroup.get('registeredRedirectUri').value ? (formGroup.get('registeredRedirectUri').value as string).split(',') : null,
      autoApprove: formGroup.get('grantType').value === grantTypeEnums.authorization_code ? !!formGroup.get('autoApprove').value : null,
      version: this.data.from && this.data.from.version,
      projectId: formGroup.get('projectId').value
    }
  }
  update() {
    this.allowError = true
    if (this.validateUpdateForm()) {
      this.clientSvc.update(this.data.from.id, this.convertToPayload(), this.changeId)
    }
  }
  private validateUpdateForm() {
    const fg = this.fis.formGroups[this.formId];
    const var0 = Validator.exist(fg.get('name').value)
    this.fis.updateError(this.formId, 'name', var0.errorMsg)
    return !var0.errorMsg
  }
  create() {
    this.allowError = true
    if (this.validateCreateForm()) {
      this.clientSvc.create(this.convertToPayload(), this.changeId)
    }
  }
  private validateCreateForm() {
    const fg = this.fis.formGroups[this.formId];

    const var0 = Validator.exist(fg.get('name').value)
    this.fis.updateError(this.formId, 'name', var0.errorMsg)

    const var1 = Validator.exist(fg.get('frontOrBackApp').value)
    this.fis.updateError(this.formId, 'frontOrBackApp', var1.errorMsg)
    let grantType = true;
    let accTokenSec = true;
    let refTokenSec = true;
    let path = true;
    let externalUrl = true;
    let clientSecret = true;
    let redirectUrl = true;
    const var2 = Validator.exist(fg.get('grantType').value)
    this.fis.updateError(this.formId, 'grantType', var2.errorMsg)
    grantType = !var2.errorMsg

    const var3 = Validator.exist(fg.get('accessTokenValiditySeconds').value)
    this.fis.updateError(this.formId, 'accessTokenValiditySeconds', var3.errorMsg)
    accTokenSec = !var3.errorMsg

    if (fg.get('refreshToken').value) {
      const var3 = Validator.exist(fg.get('refreshTokenValiditySeconds').value)
      this.fis.updateError(this.formId, 'refreshTokenValiditySeconds', var3.errorMsg)
      refTokenSec = !var3.errorMsg
    }

    if (fg.get('frontOrBackApp').value === 'FRONTEND_APP') {
      //fronend app
      if (fg.get('grantType').value === 'AUTHORIZATION_CODE') {
        const var3 = Validator.exist(fg.get('registeredRedirectUri').value)
        this.fis.updateError(this.formId, 'registeredRedirectUri', var3.errorMsg)
        redirectUrl = !var3.errorMsg
      }
    } else {
      if (fg.get('frontOrBackApp').value) {
        //backend app

        const var4 = Validator.exist(fg.get('path').value)
        this.fis.updateError(this.formId, 'path', var4.errorMsg)
        path = !var4.errorMsg

        const var5 = Validator.exist(fg.get('externalUrl').value)
        this.fis.updateError(this.formId, 'externalUrl', var5.errorMsg)
        externalUrl = !var5.errorMsg

        const var6 = Validator.exist(fg.get('clientSecret').value)
        this.fis.updateError(this.formId, 'clientSecret', var6.errorMsg)
        clientSecret = !var6.errorMsg
      } else {
        //not selected
      }
    }

    return !var0.errorMsg && !var1.errorMsg && grantType && accTokenSec
      && refTokenSec && path && externalUrl && clientSecret && redirectUrl
  }
  dismiss(event: MouseEvent) {
    this.bottomSheetRef.dismiss();
    event.preventDefault();
  }
}
