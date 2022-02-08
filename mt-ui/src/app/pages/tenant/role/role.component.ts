import { AfterViewInit, ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { TranslateService } from '@ngx-translate/core';
import { FormInfoService } from 'mt-form-builder';
import { of } from 'rxjs';
import { switchMap, take } from 'rxjs/operators';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { RoleValidator } from 'src/app/clazz/validation/aggregate/role/validator-role';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { FORM_CONFIG } from 'src/app/form-configs/role.config';
import { INewRole } from 'src/app/pages/tenant/my-roles/my-roles.component';
import { EndpointService } from 'src/app/services/endpoint.service';
import { NewRoleService } from 'src/app/services/new-role.service';
import { PermissionService } from 'src/app/services/permission.service';
@Component({
  selector: 'app-role',
  templateUrl: './role.component.html',
  styleUrls: ['./role.component.css']
})
export class RoleComponent extends Aggregate<RoleComponent, INewRole> implements OnInit, AfterViewInit, OnDestroy {
  bottomSheet: IBottomSheet<INewRole>;
  public loadRoot =undefined;
  public loadChildren ;
  public permissionFg: FormGroup = new FormGroup({})
  public apiRootId: string;
  constructor(
    public entityService: NewRoleService,
    public epSvc: EndpointService,
    public permissoinSvc: PermissionService,
    fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<RoleComponent>,
    cdr: ChangeDetectorRef,
    private translate: TranslateService
  ) {
    super('role-form', JSON.parse(JSON.stringify(FORM_CONFIG)), new RoleValidator(), bottomSheetRef, data, fis, cdr)
    this.bottomSheet = data;
    this.permissoinSvc.queryPrefix=`projectIds:${this.bottomSheet.params['projectId']}`
    this.loadRoot = this.permissoinSvc.readEntityByQuery(0, 1000, "parentId:null").pipe(switchMap(data => {
      const var0 = data.data.find(ee => ee.name === 'API_ACCESS')
      if (var0) {
        this.apiRootId = var0.id;
        return this.translate.get('API_ACCESS').pipe(switchMap((var1: string) => {
          var0.name = var1;
          return of(data)
        }))
      }
      return of(data);
    }))
    this.loadChildren = (id: string) => {
      if (id === this.apiRootId) {
        return this.permissoinSvc.readEntityByQuery(0, 1000, "parentId:" + id).pipe(switchMap(childNodes => {
          const epIds = childNodes.data.map(e => e.name)
          return this.epSvc.readEntityByQuery(0, epIds.length, 'id:' + epIds.join('.')).pipe(switchMap(resp => {
            childNodes.data.forEach(e => {
              const var0 = resp.data.find(ee => ee.id === e.name);
              e.name = var0 ? var0.name : e.name
            })
            return of(childNodes)
          }))
        }))
      } else {
        return this.permissoinSvc.readEntityByQuery(0, 1000, "parentId:" + id)
      }
    }
    this.fis.formCreated(this.formId).pipe(take(1)).subscribe(() => {
      if (this.bottomSheet.context === 'new') {
        this.fis.formGroupCollection[this.formId].get('projectId').setValue(this.bottomSheet.params['projectId'])
      }
    })
  }
  ngAfterViewInit(): void {
    if (this.bottomSheet.context === 'edit') {
      this.fis.formGroupCollection[this.formId].get('id').setValue(this.aggregate.id)
      this.fis.formGroupCollection[this.formId].get('name').setValue(this.aggregate.name)
      this.fis.formGroupCollection[this.formId].get('description').setValue(this.aggregate.description ? this.aggregate.description : '')
      this.fis.formGroupCollection[this.formId].get('projectId').setValue(this.aggregate.projectId)
      this.aggregate.permissionIds.forEach(p => {
        if (!this.permissionFg.get(p)) {
          this.permissionFg.addControl(p, new FormControl('checked'))
        } else {
          this.permissionFg.get(p).setValue('checked', { emitEvent: false })
        }
      })
      this.cdr.markForCheck()
    }
  }
  ngOnDestroy(): void {
    Object.keys(this.subs).forEach(k => { this.subs[k].unsubscribe() })
    this.fis.resetAllExcept(['summaryRoleCustomerView'])
  }
  ngOnInit() {
  }
  convertToPayload(cmpt: RoleComponent): INewRole {
    let formGroup = cmpt.fis.formGroupCollection[cmpt.formId];
    const value = cmpt.permissionFg.value
    return {
      id: formGroup.get('id').value,//value is ignored
      name: formGroup.get('name').value,
      projectId: formGroup.get('projectId').value,
      permissionIds: Object.keys(value).filter(e => value[e] === 'checked').filter(e=>e),
      description: formGroup.get('description').value ? formGroup.get('description').value : null,
      version: cmpt.aggregate && cmpt.aggregate.version
    }
  }
  update() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'update', this.fis, this, this.errorMapper))
      this.entityService.update(this.aggregate.id, this.convertToPayload(this), this.changeId)
  }
  create() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'create', this.fis, this, this.errorMapper)) {
      this.entityService.create(this.convertToPayload(this), this.changeId)
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
