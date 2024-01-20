import { MatTableDataSource } from "@angular/material/table";
import { ISumRep } from "./summary.component";
import { HttpProxyService } from "../services/http-proxy.service";

export class TableHelper<T>{
    data: MatTableDataSource<T>;
    pageNum: number;
    pageSize: number;
    totoalItemCount = 0;
    columnConfig: any;
    proxy: HttpProxyService;
    url: string;
    constructor(columnConfig: any, pageSize: number, proxy: HttpProxyService, url: string) {
        this.columnConfig = columnConfig;
        this.pageSize = pageSize
        this.proxy = proxy
        this.url = url
    }
    loadPage(pageNumber: number) {
        this.proxy.readEntityByQuery<T>(this.url, pageNumber, this.pageSize).subscribe(next => {
            this.updateSummaryData(next);
        })
    }
    refresh() {
        this.loadPage(this.pageNum)
    }
    private updateSummaryData(next: ISumRep<T>) {
        if (next.data) {
            this.data = new MatTableDataSource(next.data);
            this.totoalItemCount = next.totalItemCount;
        } else {
            this.data = new MatTableDataSource([]);
            this.totoalItemCount = 0;
        }
    }
    public displayedColumns() {
        return Object.keys(this.columnConfig)
    };
}