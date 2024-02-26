import { Component } from '@angular/core';
import { Logger } from 'src/app/misc/logger';
import { IIdBasedEntity } from 'src/app/clazz/summary.component';
import { Utility, getUrl } from 'src/app/misc/utility';
import { Validator } from 'src/app/misc/validator';
import { IEndpoint, IQueryProvider } from 'src/app/misc/interface';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ProjectService } from 'src/app/services/project.service';
import { environment } from 'src/environments/environment';
import { APP_CONSTANT, RESOURCE_NAME } from 'src/app/misc/constant';
import { BannerService } from 'src/app/services/banner.service';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { FormGroup, FormControl } from '@angular/forms';
export interface ISubRequest extends IIdBasedEntity {
  endpointId?: string,
  projectId?: string,
  replenishRate: number,
  burstCapacity: number,
}
@Component({
  selector: 'app-subscribe-request',
  templateUrl: './subscribe-request.component.html',
  styleUrls: ['./subscribe-request.component.css']
})
export class SubscribeRequestComponent {
  private url = getUrl([environment.serverUri, APP_CONSTANT.MT_AUTH_ACCESS_PATH, RESOURCE_NAME.SUBSCRIPTIONS_REQUEST])
  context: 'NEW' | 'EDIT' = 'NEW';
  public publicSubNotes: boolean = false;
  public changeId = Utility.getChangeId();
  private allowError: boolean = false;
  public projectIdErrorMsg: string = undefined;
  public replenishRateErrorMsg: string = undefined;
  public burstCapacityErrorMsg: string = undefined;
  private endpoint: IEndpoint;
  fg = new FormGroup({
    projectId: new FormControl(''),
    replenishRate: new FormControl(''),
    burstCapacity: new FormControl(''),
  });
  constructor(
    public router: RouterWrapperService,
    private projectSvc: ProjectService,
    public httpProxySvc: HttpProxyService,
    private banner: BannerService,
  ) {
    const configId = this.router.getSubRequestIdFromUrl();
    Logger.debug('config id get {}', configId)
    if (configId === 'template') {
      if (this.router.getData() === undefined) {
        this.router.navMarket()
      }
      this.endpoint = (this.router.getData() as IEndpoint)
      if (!this.endpoint.secured) {
        this.publicSubNotes = true;
        this.fg.get('replenishRate').disable()
        this.fg.get('burstCapacity').disable()
      }
      this.fg.valueChanges.subscribe(e => {
        if (this.allowError) {
          this.validateCreateRequestForm()
        }
      })
    } else {
      this.context = 'EDIT'
      if (this.router.getData() === undefined) {
        this.router.navSubRequestDashboard()
      }
      this.fg.valueChanges.subscribe(e => {
        if (this.allowError) {
          this.validateUpdateRequestForm()
        }
      })
    }
  }
  create() {
    this.allowError = true;
    if (this.validateCreateRequestForm()) {
      const payload = {
        id: '',
        endpointId: this.endpoint.id,
        projectId: this.fg.get('projectId').value,
        replenishRate: +this.fg.get('replenishRate').value,
        burstCapacity: +this.fg.get('burstCapacity').value,
        version: 0
      }
      this.httpProxySvc.createEntity(this.url, payload, this.changeId).subscribe(next => {
        this.banner.notify(!!next)
      })
    }
  }
  update() {
    this.allowError = true;
    if (this.validateUpdateRequestForm()) {
      const payload = {
        id: '',
        replenishRate: +this.fg.get('replenishRate').value,
        burstCapacity: +this.fg.get('burstCapacity').value,
        version: 0
      }
      this.httpProxySvc.updateEntity(this.url, this.router.getSubRequestIdFromUrl(), payload, this.changeId).subscribe(next => {
        this.banner.notify(next)
      })
    }
  }

  private validateCreateRequestForm() {
    Logger.debug('checking create request form')
    if (this.endpoint.secured) {
      const var0 = Validator.exist(this.fg.get('projectId').value)
      this.projectIdErrorMsg = var0.errorMsg

      Logger.trace('replenishRate value is {}', this.fg.get('replenishRate').value)
      const var1 = Validator.exist(this.fg.get('replenishRate').value)
      this.replenishRateErrorMsg = var1.errorMsg

      const var2 = Validator.exist(this.fg.get('burstCapacity').value)
      this.burstCapacityErrorMsg = var2.errorMsg
      return !var0.errorMsg && !var1.errorMsg && !var2.errorMsg
    } else {
      const var0 = Validator.exist(this.fg.get('projectId').value)
      this.projectIdErrorMsg = var0.errorMsg
      return !var0.errorMsg
    }
  }

  private validateUpdateRequestForm() {
    Logger.debug('checking update request form')
    const var1 = Validator.exist(this.fg.get('replenishRate').value)
    this.replenishRateErrorMsg = var1.errorMsg

    const var2 = Validator.exist(this.fg.get('burstCapacity').value)
    this.burstCapacityErrorMsg = var2.errorMsg
    return !var1.errorMsg && !var2.errorMsg
  }

  getMyProject() {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.projectSvc.findTenantProjects(num, size, header)
      }
    } as IQueryProvider
  }
  goBack() {
    this.context === 'EDIT' ? this.router.navSubRequestDashboard() : this.router.navApiMarket()
  }
}
