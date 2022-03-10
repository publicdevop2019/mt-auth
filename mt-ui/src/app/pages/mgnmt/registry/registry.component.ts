import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
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
export class RegistryComponent implements OnInit {
  displayedColumns: string[] = ['id','name', 'count'];
  dataSource: MatTableDataSource<{ id: string; name: string, count: number }> = new MatTableDataSource();
  batchJobConfirmed: boolean;
  constructor(httpProxy: HttpProxyService) {
    httpProxy.getRegistryStatus().subscribe(next => {
      this.dataSource.data = next;
    })
  }

  ngOnInit(): void {
  }

}
