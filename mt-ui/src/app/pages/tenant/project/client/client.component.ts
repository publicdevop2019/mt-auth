import { Component } from '@angular/core';
import { combineLatest, Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { CLIENT_TYPE, grantTypeEnums, RESOURCE_NAME } from 'src/app/misc/constant';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { Utility } from 'src/app/misc/utility';
import { Validator } from 'src/app/misc/validator';
import { IClient, IClientCreate, IOption } from 'src/app/misc/interface';
import { ProjectService } from 'src/app/services/project.service';
import { FormGroup, FormControl } from '@angular/forms';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { Logger } from 'src/app/misc/logger';
import { DeviceService } from 'src/app/services/device.service';

@Component({
  selector: 'app-client',
  templateUrl: './client.component.html',
  styleUrls: ['./client.component.css']
})
export class ClientComponent {
  private projectId = this.router.getProjectIdFromUrl()
  private url = Utility.getProjectResource(this.projectId, RESOURCE_NAME.CLIENTS)
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
  data: IClient;
  context: 'NEW' | 'EDIT' = 'NEW';
  options: IOption[] = []
  resourceNum: number = 0
  resourceSize: number = 50
  fg = new FormGroup({
    id: new FormControl({ value: '', disabled: true }),
    projectId: new FormControl({ value: '', disabled: true }),
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
    public projectSvc: ProjectService,
    public httpProxySvc: HttpProxyService,
    public router: RouterWrapperService,
    public deviceSvc: DeviceService
  ) {
    this.deviceSvc.updateDocTitle('CLIENT_DOC_TITLE')
    const clientId = this.router.getClientIdFromUrl();
    Logger.debug(clientId)
    if (clientId === 'template') {
      if (this.router.getData() === undefined) {
        this.router.navProjectHome()
      }
      if (this.context === 'NEW') {
        const createData = (this.router.getData() as IClientCreate)
        this.fg.get('projectId').setValue(this.router.getProjectIdFromUrl())
        this.fg.get('frontOrBackApp').setValue(createData.type)
        this.fg.get('name').setValue(createData.name)
        this.fg.get('grantType').setValue(['AUTHORIZATION_CODE', 'PASSWORD'])
        this.fg.get('accessTokenValiditySeconds').setValue(120)
        this.fg.get('refreshToken').setValue(true)
        this.fg.get('refreshTokenValiditySeconds').setValue(1200)
        this.fg.get('registeredRedirectUri').setValue('http://localhost:3000/user-profile')
        this.fg.get('clientSecret').setValue(Utility.getChangeId())
        if (createData.type === 'BACKEND_APP') {
          this.fg.get('resourceIndicator').setValue(true)
          this.fg.get('path').setValue(Utility.getChangeId().replace(new RegExp(/[\d-]/g), '') + '-svc')
          this.fg.get('externalUrl').setValue('http://localhost:8080/server-address')

        }
      }
    } else {
      this.context = 'EDIT'
      this.httpProxySvc.readEntityById<IClient>(this.url, clientId).subscribe(next => {
        this.data = next;
        const var0: Observable<any>[] = [];
        if (this.data.resourceIds && this.data.resourceIds.length > 0) {
          var0.push(this.httpProxySvc.readEntityByQuery(this.url, 0, this.data.resourceIds.length, 'id:' + this.data.resourceIds.join('.')))
        }
        if (var0.length === 0) {
          this.resume()
        } else {
          combineLatest(var0).pipe(take(1))
            .subscribe(next => {
              let count = -1;
              if (this.data.resourceIds && this.data.resourceIds.length > 0) {
                count++;
                const nextOptions = next[count].data.map(e => <IOption>{ label: e.name, value: e.id })
                this.options = nextOptions;
              }
              this.resume()
            })
        }
      })
    }
    this.fg.valueChanges.subscribe(() => {
      if (this.enableError) {
        this.validateUpdateForm()
      }
    })
    this.httpProxySvc.readEntityByQuery<IClient>(this.url, this.resourceNum, this.resourceSize, `resourceIndicator:1`, undefined, undefined, undefined)
      .subscribe(next => {
        next.data = next.data.filter(e => e.id !== this.data.id);
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
  }

  update() {
    this.enableError = true
    if (this.validateUpdateForm()) {
      this.httpProxySvc.updateEntity(this.url, this.data.id, this.convertToPayload(), this.changeId).subscribe(next => {
        this.deviceSvc.notify(next)
      })
    }
  }
  create() {
    this.enableError = true
    if (this.validateCreateForm()) {
      this.httpProxySvc.createEntity(this.url, this.convertToPayload(), this.changeId).subscribe(next => {
        this.deviceSvc.notify(!!next);
        this.router.navProjectClientsDashboard()
      });
    }
  }
  private resume(): void {
    const grantType = this.data.grantTypeEnums.filter(e => e !== grantTypeEnums.refresh_token);
    const value = {
      id: this.data.id,
      projectId: this.data.projectId,
      path: this.data.path ? this.data.path : '',
      externalUrl: this.data.externalUrl ? this.data.externalUrl : '',
      clientSecret: this.data.clientSecret,
      name: this.data.name,
      description: this.data.description || '',
      frontOrBackApp: this.data.types.filter(e => [CLIENT_TYPE.frontend_app, CLIENT_TYPE.backend_app].includes(e))[0],
      grantType: grantType,
      registeredRedirectUri: this.data.registeredRedirectUri ? this.data.registeredRedirectUri.join(',') : '',
      refreshToken: this.data.grantTypeEnums.find(e => e === grantTypeEnums.refresh_token),
      resourceIndicator: this.data.resourceIndicator,
      autoApprove: this.data.autoApprove,
      accessTokenValiditySeconds: this.data.accessTokenValiditySeconds,
      refreshTokenValiditySeconds: this.data.refreshTokenValiditySeconds,
      resourceId: this.data.resourceIds,
    }
    this.fg.patchValue(value)
  }

  private convertToPayload(): IClient {
    let formGroup = this.fg;
    let grants: grantTypeEnums[] = [];
    const types: CLIENT_TYPE[] = [];
    if (formGroup.get('grantType').value) {
      grants.push(...(formGroup.get('grantType').value || []));
    }
    if (grants.includes(grantTypeEnums.password) && formGroup.get('refreshToken').value) {
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
        autoApprove: grants.includes(grantTypeEnums.authorization_code) ? !!formGroup.get('autoApprove').value : null,
        version: this.data && this.data.version,
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
      refreshTokenValiditySeconds: grants.includes(grantTypeEnums.refresh_token) && formGroup.get('refreshToken').value ? (Utility.hasValue(formGroup.get('refreshTokenValiditySeconds').value) ? +formGroup.get('refreshTokenValiditySeconds').value : null) : null,
      resourceIndicator: !!formGroup.get('resourceIndicator').value,
      resourceIds: formGroup.get('resourceId').value ? formGroup.get('resourceId').value as string[] : [],
      registeredRedirectUri: grants.includes(grantTypeEnums.authorization_code) && formGroup.get('registeredRedirectUri').value ? (formGroup.get('registeredRedirectUri').value as string).split(',') : null,
      autoApprove: grants.includes(grantTypeEnums.authorization_code) ? !!formGroup.get('autoApprove').value : null,
      version: this.data && this.data.version,
      projectId: formGroup.get('projectId').value
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