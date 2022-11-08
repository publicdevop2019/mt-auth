import { ChangeDetectorRef, Component, Inject, OnInit } from '@angular/core';
import { MAT_BOTTOM_SHEET_DATA, MatBottomSheetRef } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IIdBasedEntity } from 'src/app/clazz/summary.component';
import { IProjectSimple } from 'src/app/clazz/validation/aggregate/project/interface-project';
import { ProjectValidator } from 'src/app/clazz/validation/aggregate/project/validator-project';
import { SubRequestValidator } from 'src/app/clazz/validation/aggregate/sub-request/validator-sub-request';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { FORM_CONFIG } from 'src/app/form-configs/sub-request.config';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ProjectService } from 'src/app/services/project.service';
import { SubRequestService } from 'src/app/services/sub-request.service';
import { CacheComponent } from '../../mgnmt/cache/cache.component';
export interface ISubRequest extends IIdBasedEntity {
  endpointId: string,
  projectId: string,
  maxInvokePerSecond: number,
  maxInvokePerMinute: number,
}
@Component({
  selector: 'app-sub-request',
  templateUrl: './sub-request.component.html',
  styleUrls: ['./sub-request.component.css']
})
export class SubRequestComponent extends Aggregate<SubRequestComponent, ISubRequest> implements OnInit {
  constructor(
    private projectSvc: ProjectService,
    private subReqSvc: SubRequestService,
    public httpProxySvc: HttpProxyService,
    fis: FormInfoService,
    cdr: ChangeDetectorRef,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<SubRequestComponent>,
  ) {
    super('newSubRequestForm', JSON.parse(JSON.stringify(FORM_CONFIG)), new SubRequestValidator('CLIENT'), bottomSheetRef, data, fis, cdr);
    this.fis.queryProvider[this.formId + '_' + 'projectId'] = this.getMyProject();
  }
  convertToPayload(cmpt: SubRequestComponent): ISubRequest {
    let formGroup = cmpt.fis.formGroupCollection[cmpt.formId];
    return {
      id: '',
      endpointId: cmpt.data.from.id,
      projectId: formGroup.get('projectId').value,
      maxInvokePerSecond: +formGroup.get('maxInvokePerSec').value,
      maxInvokePerMinute: +formGroup.get('maxInvokePerMin').value,
      version: 0
    }
  }
  update() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'updateSubReqValidator', this.fis, this, this.errorMapper))
    this.subReqSvc.update(this.aggregate.id, this.convertToPayload(this), this.changeId)
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
