import { Component, OnDestroy } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { TranslateService } from '@ngx-translate/core';
import { map } from 'rxjs/operators';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { CustomHttpInterceptor } from 'src/app/services/interceptors/http.interceptor';
import { TimeService } from 'src/app/services/time.service';
export interface IJob {
  id: string
  name: string
  lastExecution: number
  lastStatus: string
  type: string
  failureCount: number
  maxLockAcquireFailureAllowed: number
  failureReason: string
  failureAllowed: number
  minimumIdleTimeAllowed: number
  notifiedAdmin: boolean
}
@Component({
  selector: 'app-job',
  templateUrl: './job.component.html',
  styleUrls: []
})
export class JobComponent implements OnDestroy {
  columnList = {
    name: 'NAME',
    lastStatus: 'LAST_STATUS',
    type: 'TYPE',
    failureCount: 'FAILURE_COUNT',
    failureReason: 'FAILURE_REASON',
    failureAllowed: 'FAILURE_ALLOWED',
    notifiedAdmin: 'NOTIFIED',
    lastExecution: 'LAST_EXE_TIME',
    minimumIdleTimeAllowed: 'MINIMUM_IDLE_TIME_ALLOWED',
    maxLockAcquireFailureAllowed: 'MAX_LOCK_ACQUIRE_FAILURE_COUNT',
    action: 'JOB_ACTION',
  }
  dataSource: MatTableDataSource<IJob> = new MatTableDataSource();
  intervalRef: any;
  constructor(
    private interceptor: CustomHttpInterceptor,
    public httpProxy: HttpProxyService,
    private translate: TranslateService,
    private timeSvc: TimeService,
    private deviceSvc: DeviceService
  ) {
    this.deviceSvc.updateDocTitle('MGMT_JOB_DOC_TITLE')
    this.getStatus()
    this.intervalRef = setInterval(() => {
      this.getStatus()
    }, 5000)
  }
  ngOnDestroy(): void {
    clearInterval(this.intervalRef)
  }
  getStatus() {
    this.httpProxy.getJobStatus().subscribe(next => {
      this.dataSource.data = next;
    }, () => {
      clearInterval(this.intervalRef)
    })
  }
  parseDate(value: number) {
    return this.timeSvc.getUserFriendlyTimeDisplay(value)
  }
  resetValidationJob() {
    this.httpProxy.resetValidationJob().subscribe(() => {
      this.interceptor.openSnackbar('OPERATION_SUCCESS');
    }, () => {
      this.interceptor.openSnackbar('OPERATION_FAILED')
    })
  }
  resetJob(id: string) {
    this.httpProxy.resetJob(id).subscribe(() => {
      this.interceptor.openSnackbar('OPERATION_SUCCESS');
    }, () => {
      this.interceptor.openSnackbar('OPERATION_FAILED')
    })
  }
  isTemplate(row: IJob) {
    return row.type === 'SINGLE' && row.name.search(new RegExp(/\d+/)) === -1;
  }
  isSingle(row: IJob) {
    return row.type === 'SINGLE';
  }
  getName(row: IJob) {
    if (row.type === 'SINGLE' && !this.isTemplate(row)) {
      const index = row.name.search(new RegExp(/\d+/));
      const nextName = row.name.substring(0, index - 1)
      const instanceId = row.name.substring(index, row.name.length)
      return this.translate.get(nextName).pipe(map(e => {
        return e + '_' + instanceId
      }))
    } else {
      return this.translate.get(row.name)
    }
  }
  getNotify(data: boolean) {
    return data ? 'YES' : 'NO'
  }
  getColumnLabelValue() {
    return Object.keys(this.columnList).map(e => ({ label: this.columnList[e], value: e }))
  }
  displayedColumns() {
    return Object.keys(this.columnList)
  };
}
