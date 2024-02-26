import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatDialog } from '@angular/material/dialog';
import { FormInfoService } from 'mt-form-builder';
import { filter } from 'rxjs/operators';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { OperationConfirmDialogComponent } from 'src/app/components/operation-confirm-dialog/operation-confirm-dialog.component';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { UserService } from 'src/app/services/user.service';
import { MgmtUserComponent } from '../mgmt-user/mgmt-user.component';
import { IAuthUser, IOption } from 'src/app/misc/interface';
import { Utility } from 'src/app/misc/utility';
import { ActivatedRoute } from '@angular/router';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
@Component({
  selector: 'app-summary-user',
  templateUrl: './summary-user.component.html',
})
export class SummaryResourceOwnerComponent extends SummaryEntityComponent<IAuthUser, IAuthUser> implements OnDestroy {
  public formId = "userTableColumnConfig";
  columnList = {
    id: 'ID',
    email: 'EMAIL',
    locked: 'LOCKED',
    createdAt: 'CREATE_AT',
    edit: 'EDIT',
    token: 'REVOKE_TOKEN',
    delete: 'DELETE',
  }
  sheetComponent = MgmtUserComponent;
  public roleList: IOption[] = [];
  searchConfigs: ISearchConfig[] = [
    {
      searchLabel: 'ID',
      searchValue: 'id',
      type: 'text',
      multiple: {
        delimiter:'.'
      }
    },
    {
      searchLabel: 'EMAIL',
      searchValue: 'email',
      type: 'text',
      multiple: {
        delimiter:'.'
      }
    },
  ]
  constructor(
    public entitySvc: UserService,
    public activated: ActivatedRoute,
    public router: RouterWrapperService,
    public fis: FormInfoService,
    public bottomSheet: MatBottomSheet,
    public dialog: MatDialog,
  ) {
    super(entitySvc,activated, router, bottomSheet,fis, 2);
    this.initTableSetting();
  }
  revokeResourceOwnerToken(id: string) {
    this.entitySvc.revokeResourceOwnerToken(id);
  }
  getAuthorityList(inputs: string[]) {
    
    return inputs.map(e => <IOption>{ label: this.roleList.find(ee => ee.value === e)?this.roleList.find(ee => ee.value === e).label:'NOT_FOUND', value: e })
  }
  doBatchLock(){
    const dialogRef = this.dialog.open(OperationConfirmDialogComponent);
    let ids = this.selection.selected.map(e => e.id)
    dialogRef.afterClosed().pipe(filter(result => result)).subscribe(() => this.entitySvc.batchUpdateUserStatus(ids, 'LOCK', Utility.getChangeId()));
  }
  doBatchUnlock(){
    const dialogRef = this.dialog.open(OperationConfirmDialogComponent);
    let ids = this.selection.selected.map(e => e.id)
    dialogRef.afterClosed().pipe(filter(result => result)).subscribe(() => this.entitySvc.batchUpdateUserStatus(ids, 'UNLOCK', Utility.getChangeId()));
  }
}
