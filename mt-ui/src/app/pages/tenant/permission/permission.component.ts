import { ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IOption, IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable } from 'rxjs';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { IEndpoint } from 'src/app/clazz/validation/aggregate/endpoint/interfaze-endpoint';
import { IPermission } from 'src/app/clazz/validation/aggregate/permission/interface-permission';
import { PermissionValidator } from 'src/app/clazz/validation/aggregate/permission/validator-permission';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { FORM_CONFIG } from 'src/app/form-configs/permission.config';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyEndpointService } from 'src/app/services/my-endpoint.service';
import { MyPermissionService } from 'src/app/services/my-permission.service';

@Component({
  selector: 'app-permission',
  templateUrl: './permission.component.html',
  styleUrls: ['./permission.component.css']
})
export class PermissionComponent extends Aggregate<PermissionComponent, IPermission> implements OnInit, OnDestroy {
  bottomSheet: IBottomSheet<IPermission>;
  hasLinked = this.aggregate && this.aggregate.linkedApiPermissionIds && this.aggregate.linkedApiPermissionIds.length > 0;
  constructor(
    public entitySvc: MyPermissionService,
    public epSvc: MyEndpointService,
    public httpProxySvc: HttpProxyService,
    fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<PermissionComponent>,
    cdr: ChangeDetectorRef
  ) {
    super('permission-form', JSON.parse(JSON.stringify(FORM_CONFIG)), new PermissionValidator(), bottomSheetRef, data, fis, cdr)
    this.bottomSheet = data;
    this.entitySvc.setProjectId(this.bottomSheet.params['projectId']);

    this.epSvc.setProjectId(this.bottomSheet.params['projectId'])
    this.fis.queryProvider[this.formId + '_' + 'parentId'] = this.getParentPerm();
    this.fis.queryProvider[this.formId + '_' + 'apiId'] = this.getEndpoints();
    this.fis.formCreated(this.formId).subscribe(() => {
      if (this.bottomSheet.context === 'new') {
        this.fis.formGroupCollection[this.formId].get('projectId').setValue(this.bottomSheet.params['projectId'])
      }
      this.fis.formGroupCollection[this.formId].get('linkApi').valueChanges.subscribe(next => {
        if (next) {
          this.fis.showIfMatch(this.formId, ['apiId'])
        } else {
          this.fis.hideIfMatch(this.formId, ['apiId'])
        }
      })

      if (this.bottomSheet.context === 'edit') {
        if (this.hasLinked) {
          this.fis.showIfMatch(this.formId, ['apiId'])
        }
        const var0: Observable<any>[] = [];
        if (this.aggregate.parentId || this.hasLinked) {
          if (this.aggregate.parentId) {
            var0.push(this.entitySvc.readEntityByQuery(0, 1, 'id:' + this.aggregate.parentId))
          }
          if (this.hasLinked) {
            var0.push(this.epSvc.readEntityByQuery(0, this.aggregate.linkedApiPermissionIds.length, 'permissionId:' + this.aggregate.linkedApiPermissionIds.join('.')))
          }
          combineLatest(var0).subscribe(next => {
            if (this.aggregate.parentId) {
              this.fis.updateOption(this.formId, 'parentId', next[0].data.map(e => <IOption>{ label: e.name, value: e.id }))
            }
            if (this.hasLinked && !this.aggregate.parentId) {
              this.fis.updateOption(this.formId, 'apiId', next[0].data.map(e => <IOption>{ label: e.name, value: e.id }))
              this.fis.formGroupCollection[this.formId].get('apiId').setValue(next[0].data.map(e => e.id))
            }
            if (this.hasLinked && this.aggregate.parentId) {
              this.fis.updateOption(this.formId, 'apiId', next[1].data.map(e => <IOption>{ label: e.name, value: e.id }))
              this.fis.formGroupCollection[this.formId].get('apiId').setValue(next[1].data.map(e => e.id))
            }
            this.resumeForm()
            this.cdr.markForCheck()
          })
        } else {
          this.resumeForm()
          this.cdr.markForCheck()
        }

      }
    })
  }
  resumeForm() {
    this.fis.restore(this.formId, {
      id: this.aggregate.id,
      name: this.aggregate.name,
      parentId: this.aggregate.parentId,
      linkApi: !!this.hasLinked
    })
  }
  getParentPerm(): IQueryProvider {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.httpProxySvc.readEntityByQuery<IPermission>(this.entitySvc.entityRepo, num, size, `types:COMMON.PROJECT`, by, order, header)
      }
    } as IQueryProvider
  }
  getEndpoints(): IQueryProvider {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.httpProxySvc.readEntityByQuery<IEndpoint>(this.epSvc.entityRepo, num, size, query, by, order, header)
      }
    } as IQueryProvider
  }
  ngOnDestroy(): void {
    this.fis.reset(this.formId)
  }
  ngOnInit() {
  }
  convertToPayload(cmpt: PermissionComponent): IPermission {
    let formGroup = cmpt.fis.formGroupCollection[cmpt.formId];

    const linked = formGroup.get('linkApi').value;
    return {
      id: formGroup.get('id').value,//value is ignored
      parentId: formGroup.get('parentId').value,
      name: formGroup.get('name').value,
      projectId: formGroup.get('projectId').value,
      linkedApiIds: linked ? formGroup.get('apiId').value ? formGroup.get('apiId').value : [] : [],
      version: cmpt.aggregate && cmpt.aggregate.version
    }
  }
  update() {
    this.entitySvc.update(this.aggregate.id, this.convertToPayload(this), this.changeId)
  }
  create() {
    this.entitySvc.create(this.convertToPayload(this), this.changeId)
  }
  errorMapper(original: ErrorMessage[], cmpt: PermissionComponent) {
    return original.map(e => {
      return {
        ...e,
        formId: cmpt.formId
      }
    })
  }
}
