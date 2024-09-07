import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { filter, switchMap } from 'rxjs/operators';
import { EnterReasonDialogComponent } from 'src/app/components/enter-reason-dialog/enter-reason-dialog.component';
import { IMySubReq } from '../my-requests/my-requests.component';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { APP_CONSTANT, RESOURCE_NAME } from 'src/app/misc/constant';
import { environment } from 'src/environments/environment';
import { Utility } from 'src/app/misc/utility';
import { TableHelper } from 'src/app/clazz/table-helper';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { DeviceService } from 'src/app/services/device.service';

@Component({
  selector: 'app-my-approval',
  templateUrl: './my-approval.component.html',
  styleUrls: ['./my-approval.component.css']
})
export class MyApprovalComponent {
  private url = Utility.getUrl([environment.serverUri, APP_CONSTANT.MT_AUTH_ACCESS_PATH, RESOURCE_NAME.SUBSCRIPTIONS_REQUEST])
  columnList = {
    id: 'ID',
    projectName: 'SUB_PROJECT_NAME',
    endpointName: 'API_NAME',
    replenishRate: 'REPLENISH_RATE_APPROVE',
    burstCapacity: 'BURST_CAPACITY_APPROVE',
    approve: 'APPROVE',
    reject: 'REJECT',
  }
  public tableSource: TableHelper<IMySubReq> = new TableHelper(this.columnList, 10, this.httpSvc, this.url, 'type:PENDING_APPROVAL');
  constructor(
    public router: RouterWrapperService,
    public device: DeviceService,
    public httpSvc: HttpProxyService,
    public deviceSvc: DeviceService,
    public dialog: MatDialog
  ) {
    this.deviceSvc.updateDocTitle('MY_APPROVAL_DOC_TITLE')
    this.tableSource.loadPage(0)
  }
  approve(id: string) {
    this.httpSvc.approveSubRequest(id, Utility.getChangeId()).subscribe(() => {
      this.deviceSvc.notify(true)
      this.tableSource.refresh()
    }, () => {
      this.deviceSvc.notify(false)
    })
  }
  reject(id: string) {
    const dialogRef = this.dialog.open(EnterReasonDialogComponent, { data: {} });
    dialogRef.afterClosed().pipe(filter(e => e)).pipe(switchMap((e: string) => {
      return this.httpSvc.rejectSubRequest(id, Utility.getChangeId(), e)
    })).subscribe(() => {
      this.deviceSvc.notify(true)
      this.tableSource.refresh()
    }, () => {
      this.deviceSvc.notify(false)
    })
  }
}