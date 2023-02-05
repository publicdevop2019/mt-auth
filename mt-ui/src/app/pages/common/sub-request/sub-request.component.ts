import { ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IIdBasedEntity } from 'src/app/clazz/summary.component';
import { SubRequestValidator } from 'src/app/clazz/validation/aggregate/sub-request/validator-sub-request';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { FORM_CONFIG } from 'src/app/form-configs/sub-request.config';
import { CreateSubRequestService } from 'src/app/services/create-sub-request.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ProjectService } from 'src/app/services/project.service';
export interface ISubRequest extends IIdBasedEntity {
  endpointId: string,
  projectId: string,
  replenishRate: number,
  burstCapacity: number,
}
@Component({
  selector: 'app-sub-request',
  templateUrl: './sub-request.component.html',
  styleUrls: ['./sub-request.component.css']
})
export class SubRequestComponent extends Aggregate<SubRequestComponent, ISubRequest> implements OnInit, OnDestroy {
  public publicSubNotes: boolean=false;
  constructor(
    private projectSvc: ProjectService,
    private subReqSvc: CreateSubRequestService,
    public httpProxySvc: HttpProxyService,
    fis: FormInfoService,
    cdr: ChangeDetectorRef,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<SubRequestComponent>,
  ) {
    super('newSubRequestForm', JSON.parse(JSON.stringify(FORM_CONFIG)), new SubRequestValidator('CLIENT'), bottomSheetRef, data, fis, cdr);
    this.fis.queryProvider[this.formId + '_' + 'projectId'] = this.getMyProject();
    this.fis.formCreated(this.formId).subscribe(() => {
      if (!this.isCreate()) {
        this.fis.hideIfMatch(this.formId, ['projectId'])
        this.fis.formGroupCollection[this.formId].get('replenishRate').setValue(this.aggregate.replenishRate)
        this.fis.formGroupCollection[this.formId].get('burstCapacity').setValue(this.aggregate.burstCapacity)
        this.cdr.markForCheck()
      }
      if(!this.data.from.secured){
        this.publicSubNotes=true;
        this.fis.formGroupCollection[this.formId].get('replenishRate').setValue(0)
        this.fis.formGroupCollection[this.formId].get('burstCapacity').setValue(0)
        this.fis.disableIfMatch(this.formId, ['replenishRate','burstCapacity'])
        this.cdr.markForCheck()
      }
    })
  }
  isCreate() {
    return this.aggregate && this.aggregate.endpointId === undefined
  }
  ngOnDestroy(): void {
    this.cleanUp()
  }
  convertToPayload(cmpt: SubRequestComponent): ISubRequest {
    let formGroup = cmpt.fis.formGroupCollection[cmpt.formId];
    return {
      id: '',
      endpointId: cmpt.data.from.id,
      projectId: formGroup.get('projectId').value,
      replenishRate: +formGroup.get('replenishRate').value,
      burstCapacity: +formGroup.get('burstCapacity').value,
      version: 0
    }
  }
  update() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'updateSubReqValidator', this.fis, this, this.errorMapper)) {
      this.subReqSvc.update(this.aggregate.id, this.convertToPayload(this), this.changeId)
    }
  }
  create() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'createSubReqValidator', this.fis, this, this.errorMapper))
      this.subReqSvc.create(this.convertToPayload(this), this.changeId)
  }

  getMyProject() {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.projectSvc.findTenantProjects(num, size)
      }
    } as IQueryProvider
  }
  ngOnInit(): void {
  }
  errorMapper(original: ErrorMessage[], cmpt: SubRequestComponent) {
    return original.map(e => {
      return {
        ...e,
        formId: cmpt.formId
      }
    })
  }
}
