import { ChangeDetectorRef, Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { MatTab } from '@angular/material/tabs';
import { TranslateService } from '@ngx-translate/core';
import { FormInfoService } from 'mt-form-builder';
import { IOption, IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { RoleValidator } from 'src/app/clazz/validation/aggregate/role/validator-role';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { DynamicTreeComponent, INode } from 'src/app/components/dynamic-tree/dynamic-tree.component';
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
  public loadRootApi = undefined;
  public loadChildrenApi;
  public commonPermissionFg: FormGroup = new FormGroup({})
  public apiPermissionFg: FormGroup = new FormGroup({})
  public apiRootId: string;
  public formIdShared: string = 'shared_api';
  @ViewChild("basicTab") basicTabControl: MatTab;
  @ViewChild("commonTab") commonTabControl: MatTab;
  @ViewChild("apiTab") apiTabControl: MatTab;
  _tree: DynamicTreeComponent;
  @ViewChild("treeCommon") set tree(v: DynamicTreeComponent) {
    this._tree = v;
  }
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
    super('role-form', FORM_CONFIG, new RoleValidator(), bottomSheetRef, data, fis, cdr)
    this.bottomSheet = data;
    this.permissoinSvc.setProjectId(this.bottomSheet.params['projectId'])
    this.sharedPermSvc.setProjectId(this.bottomSheet.params['projectId'])
    this.entitySvc.setProjectId(this.bottomSheet.params['projectId'])
    this.fis.queryProvider[this.formId + '_' + 'parentId'] = this.getParents();
    this.fis.queryProvider[this.formIdShared + '_' + 'sharedApi'] = this.getShared();
    this.fis.init(this.formInfo, this.formId)
    this.fis.init(FORM_CONFIG_SHARED, this.formIdShared)
    this.loadRoot = this.permissoinSvc.readEntityByQuery(0, 1000, "parentId:null,types:COMMON").pipe(map(e => {
      e.data.forEach(ee => {
        if (ee.type === 'PROJECT') {
          (ee as INode).editable = false;
        } else {
          (ee as INode).editable = true;
        }
      })
      return e
    })).pipe(tap(() => {
      this.cdr.markForCheck()
    }));
    this.loadChildren = (id: string) => {
      return this.permissoinSvc.readEntityByQuery(0, 1000, "parentId:" + id).pipe(map(e => {
        e.data.forEach(ee => {
          (ee as INode).editable = true;
        })
        return e
      }))
    }
    this.loadRootApi = this.permissoinSvc.readEntityByQuery(0, 1000, "parentId:null,types:API").pipe(tap(() => this.cdr.markForCheck()));
    this.loadChildrenApi = (id: string) => {
      return this.permissoinSvc.readEntityByQuery(0, 1000, "parentId:" + id).pipe(map(e => {
        e.data.forEach(ee => {
          (ee as INode).editable = true;
          (ee as INode).noChildren = true;
        })
        return e
      }))
    }
    if (this.bottomSheet.context === 'new') {
      this.fis.formGroups[this.formId].get('projectId').setValue(this.bottomSheet.params['projectId'])
    }
    this.reusme()
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
        let var0: Observable<any>[] = [];
        if (this.aggregate.parentId) {
          var0.push(this.entitySvc.readEntityByQuery(0, 1, 'id:' + this.aggregate.parentId))
        }
        if ((this.aggregate.externalPermissionIds && this.aggregate.externalPermissionIds.length > 0)) {
          var0.push(this.sharedPermSvc.readEntityByQuery(0, 1, 'id:' + this.aggregate.externalPermissionIds.join('.')))
        }
        combineLatest(var0).subscribe(next => {
          if (this.aggregate.parentId) {
            this.fis.updateOption(this.formId, 'parentId', next[0].data.map(e => <IOption>{ label: e.name, value: e.id }))
            if (next.length > 1) {
              this.fis.updateOption(this.formIdShared, 'sharedApi', next[1].data.map(e => <IOption>{ label: e.name, value: e.id }))
            }
          } else {
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
    this.fis.formGroups[this.formId].get('id').setValue(this.aggregate.id)
    this.fis.formGroups[this.formId].get('name').setValue(this.aggregate.name)
    this.fis.formGroups[this.formId].get('parentId').setValue(this.aggregate.parentId)
    if (this.aggregate.systemCreate) {
      this.fis.disableIfMatch(this.formId, ['name', 'parentId'])
    }
    this.fis.formGroups[this.formId].get('description').setValue(this.aggregate.description ? this.aggregate.description : '')
    this.fis.formGroups[this.formId].get('projectId').setValue(this.aggregate.projectId);
    this.fis.formGroups[this.formIdShared].get('sharedApi').setValue(this.aggregate.externalPermissionIds);
    (this.aggregate.apiPermissionIds || []).forEach(p => {
      if (!this.apiPermissionFg.get(p)) {
        this.apiPermissionFg.addControl(p, new FormControl('checked'))
      } else {
        this.apiPermissionFg.get(p).setValue('checked', { emitEvent: false })
      }
    });
    (this.aggregate.commonPermissionIds || []).forEach(p => {
      if (!this.commonPermissionFg.get(p)) {
        this.commonPermissionFg.addControl(p, new FormControl('checked'))
      } else {
        this.commonPermissionFg.get(p).setValue('checked', { emitEvent: false })
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
    let formGroup = cmpt.fis.formGroups[cmpt.formId];
    let formGroup2 = cmpt.fis.formGroups[cmpt.formIdShared];
    const common = cmpt.commonPermissionFg.value
    const api = cmpt.apiPermissionFg.value
    return {
      id: formGroup.get('id').value,//value is ignored
      name: formGroup.get('name').value,
      parentId: formGroup.get('parentId').value || null,
      projectId: formGroup.get('projectId').value,
      commonPermissionIds: Object.keys(common).filter(e => common[e] === 'checked').filter(e => e),
      apiPermissionIds: Object.keys(api).filter(e => api[e] === 'checked').filter(e => e),
      externalPermissionIds: formGroup2.get('sharedApi').value ? formGroup2.get('sharedApi').value : [],
      description: formGroup.get('description').value ? formGroup.get('description').value : null,
      version: cmpt.aggregate && cmpt.aggregate.version//value is ignored
    }
  }
  convertToUpdatePayload(cmpt: RoleComponent): any {
    const formGroup = cmpt.fis.formGroups[cmpt.formId];
    const formGroup2 = cmpt.fis.formGroups[cmpt.formIdShared];
    const common = cmpt.commonPermissionFg.value
    const api = cmpt.apiPermissionFg.value
    let type = ''
    if (cmpt.basicTabControl.isActive) {
      type = 'BASIC'
      return {
        type: type,
        name: cmpt.aggregate.systemCreate ? cmpt.aggregate.originalName : formGroup.get('name').value,
        parentId: formGroup.get('parentId').value || null,
        description: formGroup.get('description').value ? formGroup.get('description').value : null,
        version: cmpt.aggregate && cmpt.aggregate.version
      }
    } else if (cmpt.apiTabControl.isActive) {
      type = 'API_PERMISSION'
      return {
        type: type,
        apiPermissionIds: Object.keys(api).filter(e => api[e] === 'checked').filter(e => e),
        externalPermissionIds: formGroup2.get('sharedApi').value ? formGroup2.get('sharedApi').value : [],
        version: cmpt.aggregate && cmpt.aggregate.version
      }
    } else if (cmpt.commonTabControl.isActive) {
      type = 'COMMON_PERMISSION'
      return {
        type: type,
        commonPermissionIds: Object.keys(common).filter(e => common[e] === 'checked').filter(e => e),
        version: cmpt.aggregate && cmpt.aggregate.version
      }
    }
  }
  update() {
    if (this.validateHelper.validate(this.validator, this.convertToUpdatePayload, 'update', this.fis, this, this.errorMapper)) {
      this.entitySvc.update(this.aggregate.id, this.convertToUpdatePayload(this), this.changeId)
    }
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
  commonRemove(key: string) {
    this.commonPermissionFg.get(key).setValue('unchecked')
  }
  commonPermissions() {
    const common = this.commonPermissionFg.value
    return Object.keys(common).filter(e => common[e] === 'checked').filter(e => e)
  }
  apiRemove(key: string) {
    this.apiPermissionFg.get(key).setValue('unchecked')
  }
  apiPermissions() {
    const api = this.apiPermissionFg.value
    return Object.keys(api).filter(e => api[e] === 'checked').filter(e => e)
  }
  parseId(id: string) {
    return this.aggregate.permissionDetails.find(e => e.id === id)?.name || id
  }
}
