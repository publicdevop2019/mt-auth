import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { CustomHttpInterceptor } from 'src/app/services/interceptors/http.interceptor';
export interface IJobStatus {
  name: string
  lastExecution: number
}
@Component({
  selector: 'app-job',
  templateUrl: './job.component.html',
  styleUrls: ['./job.component.css']
})
export class JobComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['name', 'lastExecution','action'];
  dataSource: MatTableDataSource<{ name: string, lastExecution: string }> = new MatTableDataSource();
  batchJobConfirmed: boolean;
  intervalRef: any;
  constructor(private interceptor: CustomHttpInterceptor, public httpProxy: HttpProxyService, private translate: TranslateService) {
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
      this.dataSource.data = next.map(e => {
        return { name: e.name, lastExecution: this.parseDate(e.lastExecution) }
      });
    }, () => {
      clearInterval(this.intervalRef)
    })
  }
  ngOnInit(): void {
  }
  parseDate(value: number) {
    let resolved: string;
    if (this.translate.currentLang === 'zhHans') {
      resolved = 'zh-Hans'
    } else {
      resolved = 'en-Us'
    }
    let resolvedUnit: 'seconds' | 'minutes' = 'seconds';
    if (DateTime.fromMillis(value).diffNow('seconds').seconds < -60) {
      resolvedUnit = 'minutes'
    }
    const parsed = DateTime.fromMillis(value).setLocale(resolved).toRelativeCalendar({ unit: resolvedUnit });
    return parsed;
  }
  resetJob() {
    this.httpProxy.resetValidationJob().subscribe(() => {
      this.interceptor.openSnackbar('OPERATION_SUCCESS');
    }, () => {
      this.interceptor.openSnackbar('OPERATION_FAILED')
    })
  }
}
