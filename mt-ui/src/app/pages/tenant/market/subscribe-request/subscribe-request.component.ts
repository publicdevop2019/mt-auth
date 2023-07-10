import { Component, Inject, OnDestroy } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { IBottomSheet, IIdBasedEntity } from 'src/app/clazz/summary.component';
import { Logger, Utility } from 'src/app/clazz/utility';
import { IEndpoint } from 'src/app/clazz/validation/endpoint.interface';
import { Validator } from 'src/app/clazz/validation/validator-next-common';
import { FORM_CONFIG } from 'src/app/form-configs/sub-request.config';
import { CreateSubRequestService } from 'src/app/services/create-sub-request.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ProjectService } from 'src/app/services/project.service';
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
export class SubscribeRequestComponent implements OnDestroy {
  public formId = 'newSubRequestForm'
  public publicSubNotes: boolean = false;
  public changeId = Utility.getChangeId();
  private allowError: boolean = false;
  constructor(
    private projectSvc: ProjectService,
    private subReqSvc: CreateSubRequestService,
    public httpProxySvc: HttpProxyService,
    public fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: IBottomSheet<IEndpoint | ISubRequest>,
    public bottomSheetRef: MatBottomSheetRef<SubscribeRequestComponent>,
  ) {
    this.fis.init(FORM_CONFIG, this.formId)
    this.fis.queryProvider[this.formId + '_' + 'projectId'] = this.getMyProject();
    if (data.context === 'new') {
      if (!(this.data.from as IEndpoint).secured) {
        this.publicSubNotes = true;
        this.fis.disableIfMatch(this.formId, ['replenishRate', 'burstCapacity'])
      }
      this.fis.formGroups[this.formId].valueChanges.subscribe(e => {
        if (this.allowError) {
          this.validateCreateRequestForm()
        }
      })
    }
    if (data.context === 'edit') {
      this.fis.hideIfMatch(this.formId, ['projectId'])
      this.fis.formGroups[this.formId].valueChanges.subscribe(e => {
        if (this.allowError) {
          this.validateUpdateRequestForm()
        }
      })
    }
  }
  ngOnDestroy(): void {
    this.fis.reset(this.formId)
  }
  create() {
    this.allowError = true;
    if (this.validateCreateRequestForm()) {
      const fg = this.fis.formGroups[this.formId]
      const payload = {
        id: '',
        endpointId: this.data.from.id,
        projectId: fg.get('projectId').value,
        replenishRate: +fg.get('replenishRate').value,
        burstCapacity: +fg.get('burstCapacity').value,
        version: 0
      }
      this.subReqSvc.create(payload, this.changeId)
    }
  }
  update() {
    this.allowError = true;
    if (this.validateUpdateRequestForm()) {
      const fg = this.fis.formGroups[this.formId]
      const payload = {
        id: '',
        replenishRate: +fg.get('replenishRate').value,
        burstCapacity: +fg.get('burstCapacity').value,
        version: 0
      }
      this.subReqSvc.update(this.data.from.id, payload, this.changeId)
    }
  }

  private validateCreateRequestForm() {
    Logger.debug('checking create request form')
    const fg = this.fis.formGroups[this.formId]
    if ((this.data.from as IEndpoint).secured) {
      const var0 = Validator.exist(fg.get('projectId').value)
      this.fis.updateError(this.formId, 'projectId', var0.errorMsg)

      Logger.trace('replenishRate value is {}', fg.get('replenishRate').value)
      const var1 = Validator.exist(fg.get('replenishRate').value)
      this.fis.updateError(this.formId, 'replenishRate', var1.errorMsg)

      const var2 = Validator.exist(fg.get('burstCapacity').value)
      this.fis.updateError(this.formId, 'burstCapacity', var2.errorMsg)
      return !var0.errorMsg && !var1.errorMsg && !var2.errorMsg
    } else {
      const var0 = Validator.exist(fg.get('projectId').value)
      this.fis.updateError(this.formId, 'projectId', var0.errorMsg)
      return !var0.errorMsg
    }
  }

  private validateUpdateRequestForm() {
    Logger.debug('checking update request form')
    const fg = this.fis.formGroups[this.formId]
    const var1 = Validator.exist(fg.get('replenishRate').value)
    this.fis.updateError(this.formId, 'replenishRate', var1.errorMsg)

    const var2 = Validator.exist(fg.get('burstCapacity').value)
    this.fis.updateError(this.formId, 'burstCapacity', var2.errorMsg)
    return !var1.errorMsg && !var2.errorMsg
  }

  getMyProject() {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.projectSvc.findTenantProjects(num, size, header)
      }
    } as IQueryProvider
  }

  dismiss(event: MouseEvent) {
    this.bottomSheetRef.dismiss();
    event.preventDefault();
  }
}
