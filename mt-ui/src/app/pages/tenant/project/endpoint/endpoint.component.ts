import { Component } from '@angular/core';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { IDomainContext } from 'src/app/clazz/summary.component';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyEndpointService } from 'src/app/services/my-endpoint.service';
import { Utility } from 'src/app/misc/utility';
import { Validator } from 'src/app/misc/validator';
import { ICacheProfile, IClient, ICorsProfile, IEndpoint, IEndpointCreate } from 'src/app/misc/interface';
import { Logger } from 'src/app/misc/logger';
import { ProjectService } from 'src/app/services/project.service';
import { FormGroup, FormControl } from '@angular/forms';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { BannerService } from 'src/app/services/banner.service';
@Component({
  selector: 'app-endpoint',
  templateUrl: './endpoint.component.html',
  styleUrls: ['./endpoint.component.css']
})
export class EndpointComponent {
  private projectId = this.router.getProjectIdFromUrl()
  private clientUrl = Utility.getProjectResource(this.projectId, RESOURCE_NAME.CLIENTS)
  changeId: string = Utility.getChangeId();
  allowError: boolean = false;

  resourceIdErrorMsg: string = undefined
  websocketErrorMsg: string = undefined
  nameErrorMsg: string = undefined
  pathErrorMsg: string = undefined
  methodErrorMsg: string = undefined
  replenishRateErrorMsg: string = undefined;
  burstCapacityErrorMsg: string = undefined;
  corsIdErrorMsg: string = undefined;

  performanceWarnning: boolean = false;
  data: IEndpoint;
  options: IOption[] = []
  corsOptions: IOption[] = []
  cacheOptions: IOption[] = []
  fg = new FormGroup({
    id: new FormControl({ value: '', disabled: true }),
    projectId: new FormControl(''),
    name: new FormControl(''),
    description: new FormControl(''),
    type: new FormControl(''),
    isWebsocket: new FormControl(''),
    resourceId: new FormControl(''),
    path: new FormControl(''),
    method: new FormControl({ value: '', disabled: true }),
    csrf: new FormControl(''),
    cors: new FormControl(false),
    corsProfile: new FormControl({ value: '', disabled: true }),
    cacheProfile: new FormControl({ value: '', disabled: true }),
    replenishRate: new FormControl(''),
    burstCapacity: new FormControl(''),
  });
  resourceIdPageNum = 0;
  resourceIdPageSize = 50;
  corsPageNum = 0;
  corsPageSize = 10;
  cachePageNum = 0;
  cachePageSize = 10;

  context: 'NEW' | 'EDIT' = 'NEW';
  private cacheUrl = Utility.getProjectResource(this.projectId, RESOURCE_NAME.CACHE)
  private corsUrl = Utility.getProjectResource(this.projectId, RESOURCE_NAME.CORS)
  constructor(
    public endpointSvc: MyEndpointService,
    public projectSvc: ProjectService,
    public httpProxySvc: HttpProxyService,
    public router: RouterWrapperService,
    public banner: BannerService
  ) {
    const endpointId = this.router.getEndpointIdFromUrl();
    if (endpointId === 'template') {
      if (this.router.getData() === undefined) {
        this.router.navProjectHome()
      }
      this.data = (this.router.getData() as IDomainContext<IEndpoint>).from
    } else {
      this.context = 'EDIT'
      this.endpointSvc.readById(endpointId).subscribe(next => {
        this.data = next;
        if (this.data.shared) {
          this.fg.get('type').setValue('PROTECTED_SHARED_API')
        } else {
          if (!this.data.external) {
            this.fg.get('type').setValue('API_PRIVATE')
          } else {
            if (this.data.secured) {
              this.fg.get('type').setValue('PROTECTED_NONE_SHARED_API')
            } else {
              this.fg.get('type').setValue('PUBLIC_APP')
            }
          }
        }
        this.resume();
      })
    }
    this.httpProxySvc.readEntityByQuery<IClient>(this.clientUrl,
      this.resourceIdPageNum, this.resourceIdPageSize, `projectIds:${this.router.getProjectIdFromUrl()},resourceIndicator:1`)
      .subscribe(next => {
        this.options = next.data.map(e => <IOption>{ label: e.name, value: e.id });
      })
    this.httpProxySvc.readEntityByQuery<ICorsProfile>(this.corsUrl,
      this.corsPageNum, this.corsPageSize)
      .subscribe(next => {
        this.corsOptions = next.data.map(e => <IOption>{ label: e.name, value: e.id });
      })
    this.httpProxySvc.readEntityByQuery<ICacheProfile>(this.cacheUrl,
      this.cachePageNum, this.cachePageSize)
      .subscribe(next => {
        this.cacheOptions = next.data.map(e => <IOption>{ label: e.name, value: e.id });
      })
    this.fg.valueChanges.subscribe(() => {
      Logger.trace('validating create form')
      if (this.allowError) {
        this.validateForm()
      }
    })

    this.fg.get('method').valueChanges.subscribe(next => {
      if ((next as string) === 'GET') {
        this.fg.get('cacheProfile').enable()
      } else {
        this.fg.get('cacheProfile').disable()
      }
    })
    this.fg.get('isWebsocket').valueChanges.subscribe(next => {
      if (next === 'yes') {
        this.fg.get('method').disable()
        this.fg.get('csrf').disable()
        this.fg.get('cors').disable()
        this.fg.get('replenishRate').disable()
        this.fg.get('burstCapacity').disable()
        this.fg.get('cacheProfile').disable()
        this.fg.get('corsProfile').disable()
        this.performanceWarnning = false;
      } else {
        this.fg.get('method').enable()
        this.fg.get('csrf').enable()
        this.fg.get('cors').enable()
        this.fg.get('replenishRate').enable()
        this.fg.get('burstCapacity').enable()
        if (this.fg.get('cors').value) {
          this.fg.get('corsProfile').enable()
        }
        if (this.fg.get('method').value === 'GET') {
          this.fg.get('cacheProfile').enable()
        }
        this.performanceWarnning = true;
      }
    })
    this.fg.get('cors').valueChanges.subscribe(next => {
      if (next) {
        this.fg.get('corsProfile').enable()
      } else {
        this.fg.get('corsProfile').disable()
      }
    })
    if (this.context === 'NEW') {
      const createData = this.router.getData() as IEndpointCreate
      this.fg.get('projectId').setValue(createData.projectId)
      this.fg.get('name').setValue(createData.name)
      this.fg.get('type').setValue(createData.type)
    }
  }
  resume(): void {
    if (this.context === 'EDIT') {
      const var0: Observable<any>[] = [];
      var0.push(this.httpProxySvc.readEntityByQuery<IClient>(this.clientUrl, 0, 1, 'id:' + this.data.resourceId))
      if (this.data.corsProfileId) {
        var0.push(this.httpProxySvc.readEntityByQuery(this.corsUrl, 0, 1, 'id:' + this.data.corsProfileId))
      }
      if (this.data.cacheProfileId) {
        var0.push(this.httpProxySvc.readEntityByQuery(this.cacheUrl, 0, 1, 'id:' + this.data.cacheProfileId))
      }
      combineLatest(var0).pipe(take(1))
        .subscribe(next => {
          let count = 0;
          this.options = next[count].data.map(e => <IOption>{ label: e.name, value: e.id });
          if (this.data.corsProfileId) {
            count++;
            this.corsOptions = next[count].data.map(e => <IOption>{ label: e.name, value: e.id });
          }
          if (this.data.cacheProfileId) {
            count++;
            this.cacheOptions = next[count].data.map(e => <IOption>{ label: e.name, value: e.id });
          }
          this.fg.patchValue(this.data);
          this.fg.get("csrf").setValue(this.data.csrfEnabled);
          this.fg.get("replenishRate").setValue(this.data.replenishRate);
          this.fg.get("burstCapacity").setValue(this.data.burstCapacity);
          this.fg.get("isWebsocket").setValue(this.data.websocket ? 'yes' : 'no');
          if (this.data.corsProfileId) {
            this.fg.get("cors").setValue(true);
            this.fg.get("corsProfile").setValue(this.data.corsProfileId);
          }
          if (this.data.cacheProfileId) {
            this.fg.get("cacheProfile").setValue(this.data.cacheProfileId);
          }
        })

    }
  }
  convertToPayload(): IEndpoint {
    let secured = false;
    let external = false;
    let shared = false;
    if (this.fg.get('type').value === 'PROTECTED_NONE_SHARED_API') {
      secured = true;
      external = true;
      shared = false;
    } else if (this.fg.get('type').value === 'PROTECTED_SHARED_API') {
      secured = true;
      external = true;
      shared = true;
    } else if (this.fg.get('type').value === 'PUBLIC_APP') {
      secured = false;
      external = true;
      shared = false;
    } else if (this.fg.get('type').value === 'API_PRIVATE') {
      secured = false;
      external = false;
      shared = false;
    }
    const isWs = this.fg.get('isWebsocket').value === 'yes'
    return {
      id: this.fg.get('id').value,
      description: this.fg.get('description').value ? this.fg.get('description').value : null,
      name: this.fg.get('name').value,
      resourceId: this.fg.get('resourceId').value,
      path: this.fg.get('path').value,
      method: isWs ? "GET" : this.fg.get('method').value,
      secured: secured,
      external: external,
      websocket: isWs,
      shared: shared,
      csrfEnabled: isWs ? null : !!this.fg.get('csrf').value,
      corsProfileId: Utility.noEmptyString(this.fg.get("corsProfile").value),
      cacheProfileId: this.fg.get('method').value === 'GET' ? Utility.noEmptyString(this.fg.get("cacheProfile").value) : null,
      replenishRate: isWs ? null : +this.fg.get("replenishRate").value,
      burstCapacity: isWs ? null : +this.fg.get("burstCapacity").value,
      version: this.data && this.data.version
    }
  }
  update() {
    this.allowError = true;
    if (this.validateForm()) {
      const payload = this.convertToPayload()
      this.endpointSvc.update(this.data.id, payload, this.changeId)
    }
  }
  create() {
    this.allowError = true;
    if (this.validateForm()) {
      this.httpProxySvc.createEntity(this.endpointSvc.entityRepo, this.convertToPayload(), this.changeId).subscribe(next => {
        this.banner.notify(!!next);
        this.router.navProjectEndpointDashboard()
      });
    }
  }

  private validateForm() {
    const var0 = Validator.exist(this.fg.get('name').value)
    Logger.debug(var0.errorMsg)
    this.fg.get('name').markAsDirty()
    this.nameErrorMsg = var0.errorMsg

    const var1 = Validator.exist(this.fg.get('resourceId').value)
    this.resourceIdErrorMsg = var1.errorMsg

    const var2 = Validator.exist(this.fg.get('isWebsocket').value)
    this.websocketErrorMsg = var2.errorMsg

    const var3 = Validator.exist(this.fg.get('path').value)
    this.pathErrorMsg = var3.errorMsg

    let method = true;
    let burst = true;
    let capacity = true;
    if (this.fg.get('isWebsocket').value === 'no') {
      const var4 = Validator.exist(this.fg.get('method').value)
      this.methodErrorMsg = var4.errorMsg
      method = !var4.errorMsg

      const var5 = Validator.exist(this.fg.get('replenishRate').value)
      this.replenishRateErrorMsg = var5.errorMsg
      burst = !var5.errorMsg

      const var6 = Validator.exist(this.fg.get('burstCapacity').value)
      this.burstCapacityErrorMsg = var6.errorMsg
      capacity = !var6.errorMsg
    } else {
      //reset error
      this.methodErrorMsg = undefined;
      this.replenishRateErrorMsg = undefined;
      this.burstCapacityErrorMsg = undefined;
      this.corsIdErrorMsg = undefined;
    }
    let cors = true;
    if (this.fg.get('isWebsocket').value === 'no' && this.fg.get('cors').value) {
      const var4 = Validator.exist(this.fg.get('corsProfile').value)
      this.corsIdErrorMsg = var4.errorMsg;
      cors = !var4.errorMsg
    } else {
      this.corsIdErrorMsg = undefined;
    }
    const result = !var0.errorMsg && !var1.errorMsg && !var2.errorMsg && !var3.errorMsg && method && burst && capacity && cors
    Logger.debug('validation {}', !var0.errorMsg)
    Logger.debug('validation {}', !var1.errorMsg)
    Logger.debug('validation {}', !var2.errorMsg)
    Logger.debug('validation {}', !var3.errorMsg)
    Logger.debug('validation {}', method)
    Logger.debug('validation {}', burst)
    Logger.debug('validation {}', capacity)
    Logger.debug('validation {}', cors)
    Logger.debug('validation result {}', result)
    return result;
  }

  getIcon() {
    if (this.fg.get('type').value === 'PROTECTED_NONE_SHARED_API') {
      return 'verified_user'
    } else if (this.fg.get('type').value === 'PROTECTED_SHARED_API') {
      return 'share'
    } else if (this.fg.get('type').value === 'PUBLIC_API') {
      return 'lock_open'
    } else {
      return 'visibility_off'
    }
  }
}

