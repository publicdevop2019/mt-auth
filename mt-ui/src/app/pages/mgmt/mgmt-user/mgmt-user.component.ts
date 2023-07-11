import { Component, Inject, OnDestroy } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { MatTableDataSource } from '@angular/material/table';
import { FormInfoService } from 'mt-form-builder';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { Utility } from 'src/app/clazz/utility';
import { IAuthUser, ILoginHistory } from 'src/app/clazz/user.interface';
import { FORM_CONFIG } from 'src/app/form-configs/mgmt-user.config';
import { MyRoleService } from 'src/app/services/my-role.service';
import { UserService } from 'src/app/services/user.service';
@Component({
  selector: 'app-user',
  templateUrl: './mgmt-user.component.html',
  styleUrls: ['./mgmt-user.component.css']
})
export class MgmtUserComponent implements OnDestroy {
  public formId = 'authUser';
  changeId = Utility.getChangeId()
  columnList = {
    loginAt: 'LOGIN_AT',
    ipAddress: 'IP_ADDRESS',
    agent: 'AGENT',
  }
  dataSource: MatTableDataSource<ILoginHistory>;
  constructor(
    public userSvc: UserService,
    public fis: FormInfoService,
    public roleSvc: MyRoleService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: IBottomSheet<IAuthUser>,
    public bottomSheetRef: MatBottomSheetRef<MgmtUserComponent>,
  ) {
    this.fis.init(FORM_CONFIG, this.formId)
    const aggregate = this.data.from
    if (this.data.from) {
      this.fis.formGroups[this.formId].get('id').setValue(aggregate.id)
      this.fis.formGroups[this.formId].get('email').setValue(aggregate.email)
      this.fis.formGroups[this.formId].get('locked').setValue(aggregate.locked)
      this.fis.formGroups[this.formId].get('createdAt').setValue(new Date(aggregate.createdAt))
      this.dataSource = new MatTableDataSource(aggregate.loginHistory);
    }
  }
  ngOnDestroy(): void {
    this.fis.reset(this.formId);
  }
  dismiss(event: MouseEvent) {
    this.bottomSheetRef.dismiss();
    event.preventDefault();
  }
  convertToPayload(cmpt: MgmtUserComponent): IAuthUser {
    let formGroup = cmpt.fis.formGroups[cmpt.formId];
    return
  }
  update() {
    const fg = this.fis.formGroups[this.formId];
    const payload: IAuthUser = {
      id: fg.get('id').value,//value is ignored
      locked: fg.get('locked').value,
      version: this.data.from.version
    }
    this.userSvc.update(this.data.from.id, payload, this.changeId)
  }
  displayedColumns(): string[] {
    return Object.keys(this.columnList)
  }
}
