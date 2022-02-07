import { AfterViewInit, ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { take } from 'rxjs/operators';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { RoleValidator } from 'src/app/clazz/validation/aggregate/role/validator-role';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { FORM_CONFIG } from 'src/app/form-configs/role.config';
import { INewRole } from 'src/app/pages/tenant/my-roles/my-roles.component';
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
  public loadChildren = (id: string) => this.permissoinSvc.readByQuery(0, 1000, "parentId:" + id)
  public permissionFg: FormGroup = new FormGroup({})
  constructor(
    public entityService: NewRoleService,
    public permissoinSvc: PermissionService,
    fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<RoleComponent>,
    cdr: ChangeDetectorRef
  ) {
    super('role-form', JSON.parse(JSON.stringify(FORM_CONFIG)), new RoleValidator(), bottomSheetRef, data, fis, cdr)
    this.bottomSheet = data;
    this.permissoinSvc.queryPrefix=`projectIds:${this.bottomSheet.params['projectId']}`
    this.loadRoot = this.permissoinSvc.readByQuery(0, 1000, `parentId:null`)
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
      this.fis.formGroupCollection[this.formId].get('permissions').setValue(this.aggregate.permissionIds)
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
      permissionIds: Object.keys(value).filter(e => value[e] === 'checked'),
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
