import { Component } from '@angular/core';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { IDomainContext } from 'src/app/clazz/summary.component';
import { CLIENT_TYPE, grantTypeEnums } from 'src/app/misc/constant';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyClientService } from 'src/app/services/my-client.service';
import { Utility } from 'src/app/misc/utility';
import { Validator } from 'src/app/misc/validator';
import { IClient, IClientCreate } from 'src/app/misc/interface';
import { ProjectService } from 'src/app/services/project.service';
import { FormGroup, FormControl } from '@angular/forms';
import { CustomHttpInterceptor } from 'src/app/services/interceptors/http.interceptor';
import { RouterWrapperService } from 'src/app/services/router-wrapper';

@Component({
  selector: 'app-client',
  templateUrl: './client.component.html',
  styleUrls: ['./client.component.css']
})
export class ClientComponent {
  formId: string;
  enableError: boolean = false;

  nameErrorMsg: string = undefined;
  grantTypeErrorMsg: string = undefined;
  accessTokenValiditySecondsErrorMsg: string = undefined;
  refreshTokenValiditySecondsErrorMsg: string = undefined;
  registeredRedirectUriErrorMsg: string = undefined;
  pathErrorMsg: string = undefined;
  externalUrlErrorMsg: string = undefined;
  clientSecretErrorMsg: string = undefined;

  changeId: string = Utility.getChangeId();
  data: IDomainContext<IClient>;
  options: IOption[] = []
  resourceNum: number = 0
  resourceSize: number = 50
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
  constructor(
    public clientSvc: MyClientService,
    public projectSvc: ProjectService,
    public httpProxySvc: HttpProxyService,
    public router: RouterWrapperService,
    public interceptor: CustomHttpInterceptor
  ) {
    this.data = this.router.getData() as IDomainContext<IClient>
    if (this.data === undefined) {
      this.router.navProjectHome()
    }
    clientSvc.setProjectId(this.data.from.projectId)
    this.fg.valueChanges.subscribe(() => {
      if (this.enableError) {
        this.validateUpdateForm()
      }
    })
    this.httpProxySvc.readEntityByQuery<IClient>(this.clientSvc.entityRepo, this.resourceNum, this.resourceSize, `resourceIndicator:1`, undefined, undefined, undefined)
      .subscribe(next => {
        this.options = next.data.map(e => {
          return {
            label: e.name,
            value: e.id
          }
        })
      })
    this.fg.get('grantType').valueChanges.subscribe((next) => {
      if ((next as string[]).includes('PASSWORD')) {
        this.fg.get('refreshToken').enable()
        if (this.fg.get('refreshToken')) {
          this.fg.get('refreshTokenValiditySeconds').enable()
        }
      } else {
        this.fg.get('refreshToken').disable()
        this.fg.get('refreshTokenValiditySeconds').disable()
      }
      if ((next as string[]).includes('AUTHORIZATION_CODE')) {
        this.fg.get('registeredRedirectUri').enable()
        this.fg.get('autoApprove').enable()
      } else {
        this.fg.get('registeredRedirectUri').disable()
        this.fg.get('autoApprove').disable()
      }
    })
    this.fg.get('refreshToken').valueChanges.subscribe((next) => {
      if (next) {
        this.fg.get('refreshTokenValiditySeconds').enable()
      } else {
        this.fg.get('refreshTokenValiditySeconds').disable()
      }
    })
    if (this.data.context === 'new') {
      const createData = this.data as IDomainContext<IClientCreate>
      this.fg.get('projectId').setValue(createData.from.projectId)
      this.fg.get('frontOrBackApp').setValue(createData.from.type)
      this.fg.get('name').setValue(createData.from.name)
      this.fg.get('grantType').setValue(['AUTHORIZATION_CODE', 'PASSWORD'])
      this.fg.get('accessTokenValiditySeconds').setValue(120)
      this.fg.get('refreshToken').setValue(true)
      this.fg.get('refreshTokenValiditySeconds').setValue(1200)
      this.fg.get('registeredRedirectUri').setValue('http://localhost:3000/user-profile')
      this.fg.get('clientSecret').setValue(Utility.getChangeId())
      if (createData.from.type === 'BACKEND_APP') {
        this.fg.get('resourceIndicator').setValue(true)
        this.fg.get('path').setValue(Utility.getChangeId().replace(new RegExp(/[\d-]/g), '')+'-svc')
        this.fg.get('externalUrl').setValue('http://localhost:8080/server-address')

      }
    }
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
              this.options = nextOptions;
            }
            this.resume()
          })
      }
    };
  }
  resume(): void {
    const grantType = this.data.from.grantTypeEnums.filter(e => e !== grantTypeEnums.refresh_token);
    const value = {
      id: this.data.from.id,
      projectId: this.data.from.projectId,
      path: this.data.from.path ? this.data.from.path : '',
      externalUrl: this.data.from.externalUrl ? this.data.from.externalUrl : '',
      clientSecret: this.data.from.clientSecret,
      name: this.data.from.name,
      description: this.data.from.description || '',
      frontOrBackApp: this.data.from.types.filter(e => [CLIENT_TYPE.frontend_app, CLIENT_TYPE.backend_app].includes(e))[0],
      grantType: grantType,
      registeredRedirectUri: this.data.from.registeredRedirectUri ? this.data.from.registeredRedirectUri.join(',') : '',
      refreshToken: this.data.from.grantTypeEnums.find(e => e === grantTypeEnums.refresh_token),
      resourceIndicator: this.data.from.resourceIndicator,
      autoApprove: this.data.from.autoApprove,
      accessTokenValiditySeconds: this.data.from.accessTokenValiditySeconds,
      refreshTokenValiditySeconds: this.data.from.refreshTokenValiditySeconds,
      resourceId: this.data.from.resourceIds,
    }
    this.fg.patchValue(value)
  }
  convertToPayload(): IClient {
    let formGroup = this.fg;
    let grants: grantTypeEnums[] = [];
    const types: CLIENT_TYPE[] = [];
    if (formGroup.get('grantType').value) {
      grants.push(...(formGroup.get('grantType').value || []));
    }
    if (formGroup.get('refreshToken').value) {
      grants.push(grantTypeEnums.refresh_token);
    }
    types.push(formGroup.get('frontOrBackApp').value);
    if (types.includes(CLIENT_TYPE.frontend_app)) {
      return {
        id: formGroup.get('id').value,
        name: formGroup.get('name').value,
        description: formGroup.get('description').value ? formGroup.get('description').value : null,
        clientSecret: formGroup.get('clientSecret').value,
        grantTypeEnums: grants,
        types: types,
        accessTokenValiditySeconds: +formGroup.get('accessTokenValiditySeconds').value,
        refreshTokenValiditySeconds: formGroup.get('refreshToken').value ? (Utility.hasValue(formGroup.get('refreshTokenValiditySeconds').value) ? +formGroup.get('refreshTokenValiditySeconds').value : null) : null,
        resourceIds: formGroup.get('resourceId').value ? formGroup.get('resourceId').value as string[] : [],
        registeredRedirectUri: formGroup.get('registeredRedirectUri').value ? (formGroup.get('registeredRedirectUri').value as string).split(',') : null,
        autoApprove: (formGroup.get('grantType').value as string[]).find(e => e === grantTypeEnums.authorization_code) ? !!formGroup.get('autoApprove').value : null,
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
      clientSecret: formGroup.get('clientSecret').value,
      grantTypeEnums: grants,
      types: types,
      accessTokenValiditySeconds: +formGroup.get('accessTokenValiditySeconds').value,
      refreshTokenValiditySeconds: formGroup.get('refreshToken').value ? (Utility.hasValue(formGroup.get('refreshTokenValiditySeconds').value) ? +formGroup.get('refreshTokenValiditySeconds').value : null) : null,
      resourceIndicator: !!formGroup.get('resourceIndicator').value,
      resourceIds: formGroup.get('resourceId').value ? formGroup.get('resourceId').value as string[] : [],
      registeredRedirectUri: formGroup.get('registeredRedirectUri').value ? (formGroup.get('registeredRedirectUri').value as string).split(',') : null,
      autoApprove: (formGroup.get('grantType').value as string[]).find(e => e === grantTypeEnums.authorization_code) ? !!formGroup.get('autoApprove').value : null,
      version: this.data.from && this.data.from.version,
      projectId: formGroup.get('projectId').value
    }
  }
  update() {
    this.enableError = true
    if (this.validateUpdateForm()) {
      this.clientSvc.update(this.data.from.id, this.convertToPayload(), this.changeId)
    }
  }
  create() {
    this.enableError = true
    if (this.validateCreateForm()) {
      this.httpProxySvc.createEntity(this.clientSvc.entityRepo, this.convertToPayload(), this.changeId).subscribe(next => {
        !!next ? this.interceptor.openSnackbar('OPERATION_SUCCESS') : this.interceptor.openSnackbar('OPERATION_FAILED');
        this.router.navProjectClientsDashboard()
    });
    }
  }

  private validateUpdateForm() {
    const var0 = Validator.exist(this.fg.get('name').value)
    this.nameErrorMsg = var0.errorMsg;
    return !var0.errorMsg
  }

  private validateCreateForm() {
    let nameResult = false;
    let grantTypeResult = false;
    let accTokenSecResult = false;
    let refTokenSecResult = false;
    let pathResult = false;
    let externalUrlResult = false;
    let clientSecretResult = false;
    let redirectUrlResult = false;

    const var0 = Validator.exist(this.fg.get('name').value)
    this.nameErrorMsg = var0.errorMsg;
    nameResult = !var0.errorMsg
    this.fg.get('name').setErrors(nameResult ? null : { hasError: true })

    const var2 = Validator.exist(this.fg.get('grantType').value)
    this.grantTypeErrorMsg = var2.errorMsg;
    grantTypeResult = !var2.errorMsg
    this.fg.get('grantType').setErrors(grantTypeResult ? null : { hasError: true })

    const var3 = Validator.exist(this.fg.get('accessTokenValiditySeconds').value)
    this.accessTokenValiditySecondsErrorMsg = var3.errorMsg;
    accTokenSecResult = !var3.errorMsg
    this.fg.get('accessTokenValiditySeconds').setErrors(accTokenSecResult ? null : { hasError: true })

    if (this.fg.get('refreshToken').value) {
      const var3 = Validator.exist(this.fg.get('refreshTokenValiditySeconds').value)
      this.refreshTokenValiditySecondsErrorMsg = var3.errorMsg;
      refTokenSecResult = !var3.errorMsg
    } else {
      refTokenSecResult = true
    }
    this.fg.get('refreshTokenValiditySeconds').setErrors(refTokenSecResult ? null : { hasError: true })

    if (this.fg.get('grantType').value === 'AUTHORIZATION_CODE') {
      const var3 = Validator.exist(this.fg.get('registeredRedirectUri').value)
      this.registeredRedirectUriErrorMsg = var3.errorMsg;
      redirectUrlResult = !var3.errorMsg
    } else {
      redirectUrlResult = true
    }
    this.fg.get('registeredRedirectUri').setErrors(redirectUrlResult ? null : { hasError: true })

    if (this.fg.get('frontOrBackApp').value === 'BACKEND_APP') {
      const var4 = Validator.exist(this.fg.get('path').value)
      this.pathErrorMsg = var4.errorMsg;
      pathResult = !var4.errorMsg

      const var5 = Validator.exist(this.fg.get('externalUrl').value)
      this.externalUrlErrorMsg = var5.errorMsg;
      externalUrlResult = !var5.errorMsg

      const var6 = Validator.exist(this.fg.get('clientSecret').value)
      this.clientSecretErrorMsg = var6.errorMsg;
      clientSecretResult = !var6.errorMsg
    } else {
      pathResult = true
      externalUrlResult = true
      clientSecretResult = true
    }
    this.fg.get('path').setErrors(pathResult ? null : { wrongValue: true })
    this.fg.get('externalUrl').setErrors(externalUrlResult ? null : { hasError: true })
    this.fg.get('clientSecret').setErrors(clientSecretResult ? null : { hasError: true })
    return !var0.errorMsg && grantTypeResult && accTokenSecResult
      && refTokenSecResult && pathResult && externalUrlResult && clientSecretResult && redirectUrlResult
  }

}