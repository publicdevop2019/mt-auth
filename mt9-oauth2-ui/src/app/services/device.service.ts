import { Injectable } from '@angular/core';
import { BreakpointObserver } from '@angular/cdk/layout';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
@Injectable({
    providedIn: 'root'
})
export class DeviceService {
    updateURLQueryParamBeforeSearch(index: number, size: number, query?: string, sortby?: string, sortOrder?: string, key?: string) {
        let params: any = { ...this.activeRouter.snapshot.queryParams };
        let sort = '';
        if (sortby && sortOrder) {
            sort = `by:${sortby},order:${sortOrder}`
        }
        if (sort) {
            params['sort'] = sort;
        }
        params['query'] = query;
        if (!query) {
            delete params['query']
        }
        params['page'] = `num:${index},size:${size}`;
        if (key) {
            params['key'] = key;
        }
        const url = this.router.createUrlTree([], { relativeTo: this.activeRouter, queryParams: params }).toString();
        this.router.navigateByUrl(url);
    }
    updateURLQueryParamPageAndSort(index: number, size: number, sortby?: string, sortOrder?: string) {
        let params: any = { ...this.activeRouter.snapshot.queryParams };
        let sort = '';
        if (sortby && sortOrder) {
            sort = `by:${sortby},order:${sortOrder}`
        }
        if (sort) {
            params['sort'] = sort;
        }
        params['page'] = `num:${index},size:${size}`;
        const url = this.router.createUrlTree([], { relativeTo: this.activeRouter, queryParams: params }).toString();
        this.router.navigateByUrl(url);
    }
    refreshSummary: Subject<any> = new Subject();
    private summaryRow = 48;
    private optionalVerticalScrollbar = 48;
    public topBar = 48;
    private contentTitle = 74.81;
    private contentTitleFilter = 65.5;
    private summaryRowHeader = 56;
    private summaryRowFooter = 56;
    constructor(
        private breakpointObserver: BreakpointObserver,
        public httpClient: HttpClient,
        private router: Router,
        private activeRouter: ActivatedRoute
    ) {
    }
    public pageSize: number = Math.floor((window.innerHeight - this.topBar - this.contentTitle - this.contentTitleFilter - this.summaryRowHeader - this.summaryRowFooter - this.optionalVerticalScrollbar) / this.summaryRow) === 0 ?
        1 :
        Math.floor((window.innerHeight - this.topBar - this.contentTitle - this.contentTitleFilter - this.summaryRowHeader - this.summaryRowFooter - this.optionalVerticalScrollbar) / this.summaryRow);
    public getParams(): { page: string, query: string, sort: string, key: string } {
        return {
            page: this.activeRouter.snapshot.queryParams.page,
            query: this.activeRouter.snapshot.queryParams.query,
            sort: this.activeRouter.snapshot.queryParams.sort,
            key: this.activeRouter.snapshot.queryParams.key
        }
    }
}