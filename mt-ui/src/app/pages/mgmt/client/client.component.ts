import { ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { CLIENT_TYPE, grantTypeEnums, IClient } from 'src/app/clazz/validation/aggregate/client/interfaze-client';
import { ClientValidator } from 'src/app/clazz/validation/aggregate/client/validator-client';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { FORM_CONFIG } from 'src/app/form-configs/client.config';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MgmtClientService } from 'src/app/services/mgmt-client.service';

@Component({
  selector: 'mgmt-app-client',
  templateUrl: './client.component.html',
  styleUrls: ['./client.component.css']
})
export class MgmtClientComponent extends Aggregate<MgmtClientComponent, IClient> implements OnDestroy, OnInit {
  bottomSheet: IBottomSheet<IClient>;
  private formCreatedOb: Observable<string>;
  constructor(
    public clientSvc: MgmtClientService,
    public httpProxySvc: HttpProxyService,
    fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<MgmtClientComponent>,
    cdr: ChangeDetectorRef
  ) {
    super('mgmtClient', JSON.parse(JSON.stringify(FORM_CONFIG)), new ClientValidator(), bottomSheetRef, data, fis, cdr);
    this.bottomSheet = data;
    this.formCreatedOb = this.fis.formCreated(this.formId);
    this.formCreatedOb.subscribe(() => {
      if (this.bottomSheet.context === 'edit') {
        this.formInfo.inputs.find(e => e.key === 'clientSecret').display = (this.aggregate.types).includes(CLIENT_TYPE.backend_app);
        this.formInfo.inputs.find(e => e.key === 'path').display = (this.aggregate.types).includes(CLIENT_TYPE.backend_app);;
        this.formInfo.inputs.find(e => e.key === 'resourceIndicator').display = (this.aggregate.types).includes(CLIENT_TYPE.backend_app);;
        this.formInfo.inputs.find(e => e.key === 'resourceId').display = (this.aggregate.types).includes(CLIENT_TYPE.backend_app);;
        this.formInfo.inputs.find(e => e.key === 'registeredRedirectUri').display = (this.aggregate.grantTypeEnums as string[] || []).includes('AUTHORIZATION_CODE');
        this.formInfo.inputs.find(e => e.key === 'refreshToken').display = (this.aggregate.grantTypeEnums as string[] || []).indexOf('PASSWORD') > -1;
        this.formInfo.inputs.find(e => e.key === 'autoApprove').display = (this.aggregate.grantTypeEnums as string[] || []).indexOf('AUTHORIZATION_CODE') > -1;
        this.formInfo.inputs.find(e => e.key === 'refreshTokenValiditySeconds').display = (this.aggregate.grantTypeEnums as string[] || []).indexOf('PASSWORD') > -1 && this.aggregate.refreshTokenValiditySeconds >= 0;
        const var0: Observable<any>[] = [];
        if (this.aggregate.resourceIds && this.aggregate.resourceIds.length > 0) {
          var0.push(this.clientSvc.readEntityByQuery(0, this.aggregate.resourceIds.length, 'id:' + this.aggregate.resourceIds.join('.')))
        }
        if (var0.length === 0) {
          this.resume()
          this.fis.disableIfNotMatch(this.formId, [])
        } else {
          combineLatest(var0).pipe(take(1))
            .subscribe(next => {
              let count = -1;
              if (this.aggregate.resourceIds && this.aggregate.resourceIds.length > 0) {
                count++;
                this.formInfo.inputs.find(e => e.key === 'resourceId').options = next[count].data.map(e => <IOption>{ label: e.name, value: e.id })
              }
              this.resume()
              this.fis.disableIfNotMatch(this.formId, [])
              this.cdr.markForCheck()
            })

        }
      };
    })
  }
  ngOnInit(): void {
  }
  resume(): void {
    const grantType: string = this.aggregate.grantTypeEnums.filter(e => e !== grantTypeEnums.refresh_token)[0];
    this.fis.formGroupCollection[this.formId].patchValue({
      id: this.aggregate.id,
      hasSecret: this.aggregate.hasSecret,
      projectId: this.aggregate.projectId,
      path: this.aggregate.path ? this.aggregate.path : '',
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
  ngOnDestroy(): void {
    Object.keys(this.subs).forEach(k => { this.subs[k].unsubscribe() })
    this.fis.reset('mgmtClient');
  }
  convertToPayload(cmpt: MgmtClientComponent): IClient {
    throw new Error('Method not implemented.');
  }
  update() {
    throw new Error('Method not implemented.');
  }
  create() {
    throw new Error('Method not implemented.');
  }
  errorMapper(original: ErrorMessage[], cmpt: MgmtClientComponent): ErrorMessage[] {
    throw new Error('Method not implemented.');
  }
}
