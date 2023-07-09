import { ChangeDetectorRef, Component, Inject, OnDestroy } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IOption, IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { CLIENT_TYPE, grantTypeEnums, IClient } from 'src/app/clazz/validation/aggregate/client/interfaze-client';
import { ClientValidator } from 'src/app/clazz/validation/aggregate/client/validator-client';
import { GRANT_TYPE_LIST } from 'src/app/clazz/validation/constant';
import { ErrorMessage, hasValue } from 'src/app/clazz/validation/validator-common';
import { FORM_CONFIG } from 'src/app/form-configs/client.config';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyClientService } from 'src/app/services/my-client.service';

@Component({
  selector: 'app-client',
  templateUrl: './client.component.html',
  styleUrls: ['./client.component.css']
})
export class ClientComponent extends Aggregate<ClientComponent, IClient> implements OnDestroy{
  bottomSheet: IBottomSheet<IClient>;
  constructor(
    public clientSvc: MyClientService,
    public httpProxySvc: HttpProxyService,
    fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<ClientComponent>,
    cdr: ChangeDetectorRef
  ) {
    super('client', FORM_CONFIG, new ClientValidator(), bottomSheetRef, data, fis, cdr);
    this.bottomSheet = data;
    clientSvc.setProjectId(this.bottomSheet.params['projectId'])
    this.fis.queryProvider[this.formId + '_' + 'resourceId'] = this.getResourceIds();
    this.fis.init(this.formInfo, this.formId)
    if (this.bottomSheet.context === 'new') {
      this.fis.formGroups[this.formId].get('projectId').setValue(this.bottomSheet.params['projectId'])
    }
    this.fis.formGroups[this.formId].get('frontOrBackApp').valueChanges.subscribe((next) => {
      this.fis.resetValue(this.formId, 'grantType', false)
      this.fis.resetValue(this.formId, 'resourceId', false)
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
    if (this.bottomSheet.context === 'edit') {
      const var0: Observable<any>[] = [];
      if (this.aggregate.resourceIds && this.aggregate.resourceIds.length > 0) {
        var0.push(this.clientSvc.readEntityByQuery(0, this.aggregate.resourceIds.length, 'id:' + this.aggregate.resourceIds.join('.')))
      }
      if (var0.length === 0) {
        this.resume()
      } else {
        combineLatest(var0).pipe(take(1))
          .subscribe(next => {
            let count = -1;
            if (this.aggregate.resourceIds && this.aggregate.resourceIds.length > 0) {
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
    const grantType: string = this.aggregate.grantTypeEnums.filter(e => e !== grantTypeEnums.refresh_token)[0];
    this.fis.formGroups[this.formId].patchValue({
      id: this.aggregate.id,
      projectId: this.aggregate.projectId,
      path: this.aggregate.path ? this.aggregate.path : '',
      externalUrl: this.aggregate.externalUrl ? this.aggregate.externalUrl : '',
      clientSecret: this.aggregate.hasSecret ? '*****' : '',
      name: this.aggregate.name,
      description: this.aggregate.description || '',
      frontOrBackApp: this.aggregate.types.filter(e => [CLIENT_TYPE.frontend_app, CLIENT_TYPE.backend_app].includes(e))[0],
      grantType: grantType,
      registeredRedirectUri: this.aggregate.registeredRedirectUri ? this.aggregate.registeredRedirectUri.join(',') : '',
      refreshToken: grantType === 'PASSWORD' ? this.aggregate.grantTypeEnums.some(e => e === grantTypeEnums.refresh_token) : false,
      resourceIndicator: this.aggregate.resourceIndicator,
      autoApprove: this.aggregate.autoApprove,
      accessTokenValiditySeconds: this.aggregate.accessTokenValiditySeconds,
      refreshTokenValiditySeconds: this.aggregate.refreshTokenValiditySeconds,
      resourceId: this.aggregate.resourceIds,
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
    Object.keys(this.subs).forEach(k => { this.subs[k].unsubscribe() })
    this.fis.reset('client');
  }
  convertToPayload(cmpt: ClientComponent): IClient {
    let formGroup = cmpt.fis.formGroups[cmpt.formId];
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
        version: cmpt.aggregate && cmpt.aggregate.version,
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
      version: cmpt.aggregate && cmpt.aggregate.version,
      projectId: formGroup.get('projectId').value
    }
  }
  update() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'rootUpdateClientCommandValidator', this.fis, this, this.errorMapper))
      this.clientSvc.update(this.aggregate.id, this.convertToPayload(this), this.changeId)
  }
  create() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'rootCreateClientCommandValidator', this.fis, this, this.errorMapper)){
      this.clientSvc.create(this.convertToPayload(this), this.changeId)
    }
  }
  errorMapper(original: ErrorMessage[], cmpt: ClientComponent) {
    return original.map(e => {
      if (e.key === 'resourceIds') {
        return {
          ...e,
          key: 'resourceId',
          formId: cmpt.formId
        }
      } else if (e.key === 'grantedAuthorities') {
        return {
          ...e,
          key: 'authority',
          formId: cmpt.formId
        }
      } else if (e.key === 'grantTypeEnums') {
        return {
          ...e,
          key: 'grantType',
          formId: cmpt.formId
        }
      } else {
        return {
          ...e,
          formId: cmpt.formId
        }
      }
    })
  }
}
