import { ChangeDetectorRef, Component, Inject, OnDestroy } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { map, tap } from 'rxjs/operators';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { Utility } from 'src/app/misc/utility';
import { INode } from 'src/app/components/dynamic-tree/dynamic-tree.component';
import { FORM_CONFIG } from 'src/app/form-configs/user.config';
import { IProjectUser } from 'src/app/misc/interface';
import { MyRoleService } from 'src/app/services/my-role.service';
import { MyUserService } from 'src/app/services/my-user.service';
@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnDestroy {
  public loadRoot;
  public changeId = Utility.getChangeId();
  public loadChildren = (id: string) => this.roleSvc.readEntityByQuery(0, 1000, "parentId:" + id).pipe(map(e => {
    e.data.forEach(ee => {
      (ee as INode).editable = true;
    })
    return e
  }))
  public formGroup: FormGroup = new FormGroup({})
  public formId: string = 'userTenant';
  constructor(
    public userSvc: MyUserService,
    public fis: FormInfoService,
    public roleSvc: MyRoleService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: IBottomSheet<IProjectUser>,
    public bottomSheetRef: MatBottomSheetRef<UserComponent>,
    public cdr: ChangeDetectorRef
  ) {
    this.roleSvc.setProjectId(this.data.params['projectId'])
    this.loadRoot = this.roleSvc.readEntityByQuery(0, 1000, "parentId:null,types:PROJECT.USER").pipe(map(e => {
      e.data.forEach(ee => {
        if (ee.roleType === 'PROJECT') {

          (ee as INode).editable = false;
        } else {

          (ee as INode).editable = true;
        }
      })
      return e
    })).pipe(tap(() => this.cdr.markForCheck()));
    this.fis.init(FORM_CONFIG, this.formId)
    this.fis.formGroups[this.formId].get('projectId').setValue(this.data.from.projectId)
    this.fis.formGroups[this.formId].get('id').setValue(this.data.from.id)
    this.fis.formGroups[this.formId].get('email').setValue(this.data.from.email);
    (this.data.from.roles || []).forEach(p => {
      if (!this.formGroup.get(p)) {
        this.formGroup.addControl(p, new FormControl('checked'))
      } else {
        this.formGroup.get(p).setValue('checked', { emitEvent: false })
      }
    })
  }
  ngOnDestroy(): void {
    this.fis.reset(this.formId)
  }
  convertToPayload(): IProjectUser {
    const formGroup = this.fis.formGroups[this.formId];
    const value = this.formGroup.value
    return {
      id: formGroup.get('id').value,//value is ignored
      roles: Object.keys(value).filter(e => value[e] === 'checked'),
      projectId: formGroup.get('projectId').value,
      version: this.data.from && this.data.from.version
    }
  }
  update() {
    this.userSvc.update(this.data.from.id, this.convertToPayload(), this.changeId)
  }
  removeRole(key: string) {
    this.formGroup.get(key).setValue('unchecked')
  }
  roles() {
    const api = this.formGroup.value
    return Object.keys(api).filter(e => api[e] === 'checked').filter(e => e)
  }
  parseId(id: string) {
    return this.data.from.roleDetails.find(e => e.id === id)?.name || id
  }
  dismiss(event: MouseEvent) {
    this.bottomSheetRef.dismiss();
    event.preventDefault();
  }
}
