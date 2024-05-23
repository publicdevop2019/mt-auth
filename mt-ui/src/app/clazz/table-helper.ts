import { MatTableDataSource } from "@angular/material/table";
import { HttpProxyService } from "../services/http-proxy.service";
import { ISumRep } from "../misc/interface";

export class TableHelper<T> {
    dataSource: MatTableDataSource<T>;
    pageNum: number;
    pageSize: number;
    totoalItemCount = 0;
    columnConfig: any;
    proxy: HttpProxyService;
    url: string;
    query: string;
    constructor(columnConfig: any, pageSize: number, proxy: HttpProxyService, url: string, query?: string) {
        this.columnConfig = columnConfig;
        this.pageSize = pageSize
        this.proxy = proxy
        this.url = url
        this.query = query
    }
    loadPage(pageNumber: number) {
        this.pageNum = pageNumber;//save for reload
        this.proxy.readEntityByQuery<T>(this.url, pageNumber, this.pageSize, this.query).subscribe(next => {
            this.updateSummaryData(next);
        })
    }
    refresh() {
        this.loadPage(this.pageNum)
    }
    private updateSummaryData(next: ISumRep<T>) {
        if (next.data) {
            this.dataSource = new MatTableDataSource(next.data);
            this.totoalItemCount = next.totalItemCount;
        } else {
            this.dataSource = new MatTableDataSource([]);
            this.totoalItemCount = 0;
        }
    }
    public displayedColumns() {
        return Object.keys(this.columnConfig)
    };
}