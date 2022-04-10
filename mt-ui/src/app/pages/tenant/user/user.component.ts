import { I } from '@angular/cdk/keycodes';
import { AfterViewInit, ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { map, take, tap } from 'rxjs/operators';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { IProjectUser } from 'src/app/clazz/validation/aggregate/user/interfaze-user';
import { UserValidator } from 'src/app/clazz/validation/aggregate/user/validator-user';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { INode } from 'src/app/components/dynamic-tree/dynamic-tree.component';
import { FORM_CONFIG } from 'src/app/form-configs/user.config';
import { MyRoleService } from 'src/app/services/my-role.service';
import { MyUserService } from 'src/app/services/my-user.service';
@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent extends Aggregate<UserComponent, IProjectUser> implements OnInit, AfterViewInit, OnDestroy {
  create(): void {
    throw new Error('Method not implemented.');
  }
  public loadRoot;
  public loadChildren = (id: string) => this.roleSvc.readEntityByQuery(0, 1000, "parentId:" + id).pipe(map(e => {
    e.data.forEach(ee => {
      (ee as INode).editable = true;
    })
    return e
  }))
  public formGroup: FormGroup = new FormGroup({})
  bottomSheet: IBottomSheet<IProjectUser>;
  constructor(
    public userSvc: MyUserService,
    fis: FormInfoService,
    public roleSvc: MyRoleService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<UserComponent>,
    cdr: ChangeDetectorRef
  ) {
    super('resourceOwner', JSON.parse(JSON.stringify(FORM_CONFIG)), new UserValidator(), bottomSheetRef, data, fis, cdr);
    this.bottomSheet = data;
    this.roleSvc.setProjectId(this.bottomSheet.params['projectId'])
    this.loadRoot = this.roleSvc.readEntityByQuery(0, 1000, "parentId:null,types:PROJECT.USER").pipe(map(e => {
      e.data.forEach(ee => {
        if (ee.roleType === 'PROJECT') {

          (ee as INode).editable = false;
        } else {

          (ee as INode).editable = true;
        }
      })
      return e
    })).pipe(tap(() => this.cdr.markForCheck()));;
    this.fis.formCreated(this.formId).pipe(take(1)).subscribe(() => {
      if (this.bottomSheet.context === 'new') {
        this.fis.formGroupCollection[this.formId].get('projectId').setValue(this.bottomSheet.from.projectId)
      }
    })
  }
  ngAfterViewInit(): void {
    if (this.bottomSheet.context === 'edit') {
      (this.aggregate.roles || []).forEach(p => {
        if (!this.formGroup.get(p)) {
          this.formGroup.addControl(p, new FormControl('checked'))
        } else {
          this.formGroup.get(p).setValue('checked', { emitEvent: false })
        }
      })
      this.fis.formGroupCollection[this.formId].get('id').setValue(this.aggregate.id)
      this.fis.formGroupCollection[this.formId].get('email').setValue(this.aggregate.email)
      this.cdr.markForCheck()
    }
  }
  ngOnDestroy(): void {
    this.cleanUp()
  }
  ngOnInit() {
  }
  convertToPayload(cmpt: UserComponent): IProjectUser {
    let formGroup = cmpt.fis.formGroupCollection[cmpt.formId];
    const value = cmpt.formGroup.value
    return {
      id: formGroup.get('id').value,//value is ignored
      roles: Object.keys(value).filter(e => value[e] === 'checked'),
      projectId: formGroup.get('projectId').value,
      version: cmpt.aggregate && cmpt.aggregate.version
    }
  }
  update() {
    this.userSvc.update(this.aggregate.id, this.convertToPayload(this), this.changeId)
  }
  errorMapper(original: ErrorMessage[], cmpt: UserComponent) {
    return original.map(e => {
      return {
        ...e,
        formId: cmpt.formId
      }
    })
  }
  removeRole(key: string) {
    this.formGroup.get(key).setValue('unchecked')
  }
  roles() {
    const api = this.formGroup.value
    return Object.keys(api).filter(e => api[e] === 'checked').filter(e => e)
  }
  parseId(id: string) {
    return this.aggregate.roleDetails.find(e => e.id === id)?.name || id
  }
}
