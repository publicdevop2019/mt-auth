import { ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { TranslateService } from '@ngx-translate/core';
import { FormInfoService } from 'mt-form-builder';
import { IForm, IOption, IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable, Subscription } from 'rxjs';
import { take, tap } from 'rxjs/operators';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { RoleValidator } from 'src/app/clazz/validation/aggregate/role/validator-role';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { FORM_CONFIG, FORM_CONFIG_SHARED } from 'src/app/form-configs/role.config';
import { INewRole } from 'src/app/pages/tenant/my-roles/my-roles.component';
import { EndpointService } from 'src/app/services/endpoint.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyPermissionService } from 'src/app/services/my-permission.service';
import { MyRoleService } from 'src/app/services/my-role.service';
import { SharedPermissionService } from 'src/app/services/shared-permission.service';
@Component({
  selector: 'app-role',
  templateUrl: './role.component.html',
  styleUrls: ['./role.component.css']
})
export class RoleComponent extends Aggregate<RoleComponent, INewRole> implements OnInit, OnDestroy {
  bottomSheet: IBottomSheet<INewRole>;
  public loadRoot = undefined;
  public loadChildren;
  public permissionFg: FormGroup = new FormGroup({})
  public apiRootId: string;
  public formIdShared: string = 'shared_api';
  public formInfoShared: IForm = JSON.parse(JSON.stringify(FORM_CONFIG_SHARED));
  constructor(
    public entitySvc: MyRoleService,
    public epSvc: EndpointService,
    public permissoinSvc: MyPermissionService,
    public httpProxySvc: HttpProxyService,
    public sharedPermSvc: SharedPermissionService,
    fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<RoleComponent>,
    cdr: ChangeDetectorRef,
    private translate: TranslateService
  ) {
    super('role-form', JSON.parse(JSON.stringify(FORM_CONFIG)), new RoleValidator(), bottomSheetRef, data, fis, cdr)
    this.bottomSheet = data;
    this.permissoinSvc.setProjectId(this.bottomSheet.params['projectId'])

    this.entitySvc.setProjectId(this.bottomSheet.params['projectId'])
    this.fis.queryProvider[this.formId + '_' + 'parentId'] = this.getParents();
    this.fis.queryProvider[this.formIdShared + '_' + 'sharedApi'] = this.getShared();

    this.loadRoot = this.permissoinSvc.readEntityByQuery(0, 1000, "parentId:null").pipe(tap(() => this.cdr.markForCheck()));
    this.loadChildren = (id: string) => {
      return this.permissoinSvc.readEntityByQuery(0, 1000, "parentId:" + id)
    }
    this.fis.formCreated(this.formId).pipe(take(1)).subscribe(() => {
      if (this.bottomSheet.context === 'new') {
        this.fis.formGroupCollection[this.formId].get('projectId').setValue(this.bottomSheet.params['projectId'])
      }
      this.reusme()
    })
  }
  getParents(): IQueryProvider {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.httpProxySvc.readEntityByQuery<INewRole>(this.entitySvc.entityRepo, num, size, `types:PROJECT.USER`, by, order, header)
      }
    } as IQueryProvider
  }
  getShared(): IQueryProvider {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.httpProxySvc.readEntityByQuery<INewRole>(this.sharedPermSvc.entityRepo, num, size, undefined, by, order, header)
      }
    } as IQueryProvider
  }
  reusme(): void {
    if (this.bottomSheet.context === 'edit') {
      if (this.aggregate.parentId || (this.aggregate.externalPermissionIds && this.aggregate.externalPermissionIds.length > 0)) {
        let var0:Observable<any>[]=[];
        if(this.aggregate.parentId){
          var0.push(this.entitySvc.readEntityByQuery(0, 1, 'id:' + this.aggregate.parentId))
        }
        if((this.aggregate.externalPermissionIds && this.aggregate.externalPermissionIds.length > 0)){
          var0.push(this.sharedPermSvc.readEntityByQuery(0, 1, 'id:' + this.aggregate.externalPermissionIds.join('.')))
        }
        combineLatest(var0).subscribe(next => {
          if(this.aggregate.parentId){
            this.fis.updateOption(this.formId, 'parentId', next[0].data.map(e => <IOption>{ label: e.name, value: e.id }))
            if(next.length>1){
              this.fis.updateOption(this.formIdShared, 'sharedApi', next[1].data.map(e => <IOption>{ label: e.name, value: e.id }))
            }
          }else{
            this.fis.updateOption(this.formIdShared, 'sharedApi', next[0].data.map(e => <IOption>{ label: e.name, value: e.id }))
          }
          this.resumeForm()
        })
      } else {
        this.resumeForm()
      }
    }
  }
  resumeForm() {
    this.fis.formGroupCollection[this.formId].get('id').setValue(this.aggregate.id)
    this.fis.formGroupCollection[this.formId].get('name').setValue(this.aggregate.name)
    this.fis.formGroupCollection[this.formId].get('parentId').setValue(this.aggregate.parentId)
    if (this.aggregate.systemCreate) {
      this.fis.disableIfMatch(this.formId, ['name', 'parentId'])
    }
    this.fis.formGroupCollection[this.formId].get('description').setValue(this.aggregate.description ? this.aggregate.description : '')
    this.fis.formGroupCollection[this.formId].get('projectId').setValue(this.aggregate.projectId);
    this.fis.formGroupCollection[this.formIdShared].get('sharedApi').setValue(this.aggregate.externalPermissionIds);
    (this.aggregate.permissionIds || []).forEach(p => {
      if (!this.permissionFg.get(p)) {
        this.permissionFg.addControl(p, new FormControl('checked'))
      } else {
        this.permissionFg.get(p).setValue('checked', { emitEvent: false })
      }
    })
    this.cdr.markForCheck()
  }
  ngOnDestroy(): void {
    Object.keys(this.subs).forEach(k => { this.subs[k].unsubscribe() })
    this.fis.reset(this.formId)
    this.fis.reset(this.formIdShared)
  }
  ngOnInit() {
  }
  convertToPayload(cmpt: RoleComponent): INewRole {
    let formGroup = cmpt.fis.formGroupCollection[cmpt.formId];
    let formGroup2 = cmpt.fis.formGroupCollection[cmpt.formIdShared];
    const value = cmpt.permissionFg.value
    return {
      id: formGroup.get('id').value,//value is ignored
      name: cmpt.bottomSheet.context === 'edit' ? (cmpt.aggregate.systemCreate ? cmpt.aggregate.originalName : formGroup.get('name').value) : formGroup.get('name').value,
      parentId: formGroup.get('parentId').value || null,
      projectId: formGroup.get('projectId').value,
      permissionIds: Object.keys(value).filter(e => value[e] === 'checked').filter(e => e),
      externalPermissionIds: formGroup2.get('sharedApi').value,
      description: formGroup.get('description').value ? formGroup.get('description').value : null,
      version: cmpt.aggregate && cmpt.aggregate.version
    }
  }
  update() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'update', this.fis, this, this.errorMapper))
      this.entitySvc.update(this.aggregate.id, this.convertToPayload(this), this.changeId)
  }
  create() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'create', this.fis, this, this.errorMapper)) {
      this.entitySvc.create(this.convertToPayload(this), this.changeId)
    }
  }
  errorMapper(original: ErrorMessage[], cmpt: RoleComponent) {
    return original.map(e => {
      return {
        ...e,
        formId: cmpt.formId
      }
    })
  }
}
