import { ChangeDetectorRef, Component, Inject, OnDestroy } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IOption, IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable } from 'rxjs';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { Utility } from 'src/app/clazz/utility';
import { IEndpoint } from 'src/app/clazz/endpoint.interface';
import { IPermission } from 'src/app/clazz/permission.interface';
import { Validator } from 'src/app/clazz/validator-next-common';
import { FORM_CONFIG } from 'src/app/form-configs/permission.config';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyEndpointService } from 'src/app/services/my-endpoint.service';
import { MyPermissionService } from 'src/app/services/my-permission.service';

@Component({
  selector: 'app-permission',
  templateUrl: './permission.component.html',
  styleUrls: ['./permission.component.css']
})
export class PermissionComponent implements OnDestroy {
  public formId = 'permission-form'
  public allowError = false;
  public changeId = Utility.getChangeId();
  hasLinked = this.data.from && this.data.from.linkedApiPermissionIds && this.data.from.linkedApiPermissionIds.length > 0;
  constructor(
    public entitySvc: MyPermissionService,
    public epSvc: MyEndpointService,
    public httpProxySvc: HttpProxyService,
    public fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: IBottomSheet<IPermission>,
    public bottomSheetRef: MatBottomSheetRef<PermissionComponent>,
    cdr: ChangeDetectorRef
  ) {
    this.entitySvc.setProjectId(this.data.params['projectId']);
    this.epSvc.setProjectId(this.data.params['projectId'])

    this.fis.queryProvider[this.formId + '_' + 'parentId'] = this.getParentPerm();
    this.fis.queryProvider[this.formId + '_' + 'apiId'] = this.getEndpoints();
    this.fis.init(FORM_CONFIG, this.formId)
    if (this.data.context === 'new') {
      this.fis.formGroups[this.formId].get('projectId').setValue(this.data.params['projectId'])
      this.fis.formGroups[this.formId].valueChanges.subscribe(() => {
        if (this.allowError) {
          this.validateCreateForm()
        }
      })
    }
    if (this.data.context === 'edit') {
      this.fis.formGroups[this.formId].valueChanges.subscribe(() => {
        if (this.allowError) {
          this.validateUpdateForm()
        }
      })
    }
    this.fis.formGroups[this.formId].get('linkApi').valueChanges.subscribe(next => {
      if (next) {
        this.fis.showIfMatch(this.formId, ['apiId'])
      } else {
        this.fis.hideIfMatch(this.formId, ['apiId'])
      }
    })

    if (this.data.context === 'edit') {
      if (this.hasLinked) {
        this.fis.showIfMatch(this.formId, ['apiId'])
      }
      const var0: Observable<any>[] = [];
      if (this.data.from.parentId || this.hasLinked) {
        if (this.data.from.parentId) {
          var0.push(this.entitySvc.readEntityByQuery(0, 1, 'id:' + this.data.from.parentId))
        }
        if (this.hasLinked) {
          var0.push(this.epSvc.readEntityByQuery(0, this.data.from.linkedApiPermissionIds.length, 'permissionId:' + this.data.from.linkedApiPermissionIds.join('.')))
        }
        combineLatest(var0).subscribe(next => {
          if (this.data.from.parentId) {
            this.fis.updateOption(this.formId, 'parentId', next[0].data.map(e => <IOption>{ label: e.name, value: e.id }))
          }
          if (this.hasLinked && !this.data.from.parentId) {
            this.fis.updateOption(this.formId, 'apiId', next[0].data.map(e => <IOption>{ label: e.name, value: e.id }))
            this.fis.formGroups[this.formId].get('apiId').setValue(next[0].data.map(e => e.id))
          }
          if (this.hasLinked && this.data.from.parentId) {
            this.fis.updateOption(this.formId, 'apiId', next[1].data.map(e => <IOption>{ label: e.name, value: e.id }))
            this.fis.formGroups[this.formId].get('apiId').setValue(next[1].data.map(e => e.id))
          }
          this.resumeForm()
        })
      } else {
        this.resumeForm()
      }
    }
  }
  private resumeForm() {
    this.fis.restore(this.formId, {
      id: this.data.from.id,
      name: this.data.from.name,
      parentId: this.data.from.parentId,
      linkApi: !!this.hasLinked
    })
  }
  getParentPerm(): IQueryProvider {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.httpProxySvc.readEntityByQuery<IPermission>(this.entitySvc.entityRepo, num, size, `types:COMMON`, by, order, header)
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
  convertToPayload(): IPermission {
    const fg = this.fis.formGroups[this.formId];
    const linked = fg.get('linkApi').value;
    return {
      id: fg.get('id').value,//value is ignored
      parentId: fg.get('parentId').value ? fg.get('parentId').value : null,
      name: fg.get('name').value,
      projectId: fg.get('projectId').value,
      linkedApiIds: linked ? fg.get('apiId').value ? fg.get('apiId').value : [] : [],
      version: this.data.from && this.data.from.version
    }
  }
  update() {
    this.allowError = true
    if (this.validateUpdateForm()) {
      this.entitySvc.update(this.data.from.id, this.convertToPayload(), this.changeId)
    }
  }
  create() {
    this.allowError = true
    if (this.validateCreateForm()) {
      this.entitySvc.create(this.convertToPayload(), this.changeId)
    }
  }
  dismiss(event: MouseEvent) {
    this.bottomSheetRef.dismiss();
    event.preventDefault();
  }
  private validateCreateForm() {
    const fg = this.fis.formGroups[this.formId];
    const var0 = Validator.exist(fg.get('name').value)
    this.fis.updateError(this.formId, 'name', var0.errorMsg)
    return !var0.errorMsg
  }
  private validateUpdateForm() {
    const fg = this.fis.formGroups[this.formId];
    const var0 = Validator.exist(fg.get('name').value)
    this.fis.updateError(this.formId, 'name', var0.errorMsg)
    return !var0.errorMsg
  }
}
