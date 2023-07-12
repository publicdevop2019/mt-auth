import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { TranslateService } from '@ngx-translate/core';
import { Utility } from 'src/app/misc/utility';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
@Component({
  selector: 'app-proxy-check',
  templateUrl: './proxy-check.component.html',
  styleUrls: ['./proxy-check.component.css']
})
export class CacheControlComponent implements OnInit {
  displayedColumns: string[] = ['instance', 'value', 'result'];
  private cacheChangeId = Utility.getChangeId();
  dataSource: MatTableDataSource<{ name: string; value: string }>;
  constructor(protected httpProxySvc: HttpProxyService, private _snackBar: MatSnackBar, private translate: TranslateService) { }
  ngOnInit(): void {
  }
  sendReloadRequest() {
    this.httpProxySvc.sendReloadRequest(this.cacheChangeId).subscribe(_ => {
      this.openSnackbar('CACHE_RELOAD_MSG_SENT')
    })
  }
  openSnackbar(message: string) {
    this.translate.get(message).subscribe(next => {
      this._snackBar.open(next, 'OK', {
        duration: 5000,
      });
    })
  }
  checkStatus() {
    this.httpProxySvc.checkSum().subscribe(next => {
      const var0 = Object.keys(next.proxyValue).map(e => ({ name: e, value: next.proxyValue[e] }))
      this.dataSource = new MatTableDataSource([{ name: 'HOST', value: next.hostValue }, ...var0]);
    }, error => {
      this.dataSource = new MatTableDataSource([]);
    }
    )
  }
  getResult(value: string) {
    return this.dataSource.data.find(e => e.name === 'HOST').value === value
  }
}
