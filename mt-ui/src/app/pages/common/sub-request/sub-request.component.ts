import { ChangeDetectorRef, Component, Inject, OnInit } from '@angular/core';
import { MAT_BOTTOM_SHEET_DATA, MatBottomSheetRef } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IIdBasedEntity } from 'src/app/clazz/summary.component';
import { IProjectSimple } from 'src/app/clazz/validation/aggregate/project/interface-project';
import { ProjectValidator } from 'src/app/clazz/validation/aggregate/project/validator-project';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { FORM_CONFIG } from 'src/app/form-configs/sub-request.config';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ProjectService } from 'src/app/services/project.service';
import { CacheComponent } from '../../mgnmt/cache/cache.component';
interface ISubRequest extends IIdBasedEntity {
  projectName: string,
  maxInvokePerSecond: number,
  maxInvokePerMinute: number,
}
@Component({
  selector: 'app-sub-request',
  templateUrl: './sub-request.component.html',
  styleUrls: ['./sub-request.component.css']
})
export class SubRequestComponent extends Aggregate<SubRequestComponent, ISubRequest> implements OnInit {
  convertToPayload(cmpt: SubRequestComponent): ISubRequest {
    let formGroup = cmpt.fis.formGroupCollection[cmpt.formId];
    return {
      id: this.data,
      projectName: formGroup.get('projectId').value,
      maxInvokePerSecond: +formGroup.get('maxInvokePerSec').value,
      maxInvokePerMinute: +formGroup.get('maxInvokePerMin').value,
      version: 0
    }
  }
  create(): void {
    throw new Error('Method not implemented.');
  }
  update(): void {
    throw new Error('Method not implemented.');
  }
  
  constructor(
    private projectSvc: ProjectService,
    public httpProxySvc: HttpProxyService,
    fis: FormInfoService,
    cdr: ChangeDetectorRef,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<SubRequestComponent>,
  ) {
    super('newSubRequestForm', JSON.parse(JSON.stringify(FORM_CONFIG)), undefined, bottomSheetRef, data, fis, cdr);
    this.fis.queryProvider[this.formId + '_' + 'projectId'] = this.getMyProject();
  }

    getMyProject(){
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
