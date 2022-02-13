import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatDialog } from '@angular/material/dialog';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { ISumRep, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { DeviceService } from 'src/app/services/device.service';
import { UserService } from 'src/app/services/user.service';
import { ResourceOwnerComponent } from '../user/user.component';
import { OperationConfirmDialogComponent } from 'src/app/components/operation-confirm-dialog/operation-confirm-dialog.component';
import { filter, take } from 'rxjs/operators';
import * as UUID from 'uuid/v1';
import { IResourceOwner } from 'src/app/clazz/validation/aggregate/user/interfaze-user';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { combineLatest } from 'rxjs';
import { FormInfoService } from 'mt-form-builder';
@Component({
  selector: 'app-summary-resource-owner',
  templateUrl: './summary-resource-owner.component.html',
})
export class SummaryResourceOwnerComponent extends SummaryEntityComponent<IResourceOwner, IResourceOwner> implements OnDestroy {
  public formId = "userTableColumnConfig";
  columnList = {
    id: 'ID',
    email: 'EMAIL',
    grantedAuthorities: 'GRANTED_AUTHORITIES',
    locked: 'LOCKED',
    createdAt: 'CREATE_AT',
    edit: 'EDIT',
    token: 'REVOKE_TOKEN',
    delete: 'DELETE',
  }
  sheetComponent = ResourceOwnerComponent;
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
    public deviceSvc: DeviceService,
    public fis: FormInfoService,
    public bottomSheet: MatBottomSheet,
    public dialog: MatDialog,
  ) {
    super(entitySvc, deviceSvc, bottomSheet,fis, 2);
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
    dialogRef.afterClosed().pipe(filter(result => result)).subscribe(() => this.entitySvc.batchUpdateUserStatus(ids, 'LOCK', UUID()));
  }
  doBatchUnlock(){
    const dialogRef = this.dialog.open(OperationConfirmDialogComponent);
    let ids = this.selection.selected.map(e => e.id)
    dialogRef.afterClosed().pipe(filter(result => result)).subscribe(() => this.entitySvc.batchUpdateUserStatus(ids, 'UNLOCK', UUID()));
  }
}
