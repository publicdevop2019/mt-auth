import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatDialog } from '@angular/material/dialog';
import { FormInfoService } from 'mt-form-builder';
import { filter, switchMap, switchMapTo } from 'rxjs/operators';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { EnterReasonDialogComponent } from 'src/app/components/enter-reason-dialog/enter-reason-dialog.component';
import { DeviceService } from 'src/app/services/device.service';
import { MySubRequestService } from 'src/app/services/my-sub-request.service';
import { IMySubReq } from '../my-sub-req/my-sub-req.component';
import { SubRequestComponent } from '../sub-request/sub-request.component';

@Component({
  selector: 'app-pending-sub-req',
  templateUrl: './pending-sub-req.component.html',
  styleUrls: ['./pending-sub-req.component.css']
})
export class PendingSubReqComponent extends SummaryEntityComponent<IMySubReq, IMySubReq> implements OnDestroy {
  public formId = "pendingSubReqTableColumnConfig";
  columnList = {
    id: 'ID',
    projectName: 'SUB_PROJECT_NAME',
    endpointName: 'API_NAME',
    maxInvokePerSec: 'MAX_INVOKE_PER_SEC',
    maxInvokePerMin: 'MAX_INVOKE_PER_MIN',
    approve: 'APPROVE',
    reject: 'REJECT',
  }
  sheetComponent = SubRequestComponent;
  constructor(
    public entitySvc: MySubRequestService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public fis: FormInfoService,
    public dialog: MatDialog
  ) {
    super(entitySvc, deviceSvc, bottomSheet, fis, 0);
    //manually handle search since no search component is here
    this.doSearch({ value: 'type:PENDING_APPROVAL', key: 'type', resetPage: false });
    const sub = this.deviceSvc.refreshSummary.subscribe(() => {
      this.doSearch({ value: 'type:PENDING_APPROVAL', key: 'type', resetPage: false })
    })
    this.subs.add(sub)
  }
  approve(id: string) {
    this.entitySvc.approveSubRequest(id).subscribe(() => {
      this.entitySvc.notify(true)
      this.doSearch({ value: 'type:PENDING_APPROVAL', key: 'type', resetPage: false })
    }, () => {
      this.entitySvc.notify(false)
    })
  }
  reject(id: string) {
    const dialogRef = this.dialog.open(EnterReasonDialogComponent, { data: {} });
    dialogRef.afterClosed().pipe(filter(e=>e)).pipe(switchMap((e: string) => {
      return this.entitySvc.rejectSubRequest(id, e)
    })).subscribe(() => {
      this.entitySvc.notify(true)
      this.doSearch({ value: 'type:PENDING_APPROVAL', key: 'type', resetPage: false })
    }, () => {
      this.entitySvc.notify(false)
    })
  }
}