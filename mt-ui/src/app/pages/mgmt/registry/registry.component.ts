import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
export interface IRegistryInstance {
  id: string;
  name: string;
  count: number;
}
@Component({
  selector: 'app-registry',
  templateUrl: './registry.component.html',
  styleUrls: ['./registry.component.css']
})
export class RegistryComponent{
  displayedColumns: string[] = ['id','name', 'count'];
  dataSource: MatTableDataSource<{ id: string; name: string, count: number }> = new MatTableDataSource();
  batchJobConfirmed: boolean;
  constructor(
    httpProxy: HttpProxyService,
    private deviceSvc: DeviceService
  ) {
    this.deviceSvc.updateDocTitle('MGMT_REGISTRY_DOC_TITLE')
    httpProxy.getRegistryStatus().subscribe(next => {
      this.dataSource.data = next;
    })
  }
}
