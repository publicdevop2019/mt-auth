import { Injectable } from '@angular/core';
import { ActivatedRoute, NavigationExtras, Router } from '@angular/router';
import { Logger } from '../misc/logger';
@Injectable({
    providedIn: 'root'
})
export class RouterWrapperService {
    constructor(private router: Router) {
    }
    public getUrl() {
        return this.router.url
    }
    public getData() {
        return this.router.getCurrentNavigation().extras.state
    }
    public getParam() {
        return this.router.routerState.snapshot.root.queryParams;
    }
    public getProjectId() {
        return this.router.url.split('/')[2];
    }
    public navMfa(params: NavigationExtras) {
        this.router.navigate(['/mfa'], params);
    }
    public navLogin(params: NavigationExtras) {
        this.router.navigate(['/login'], params);
    }
    public navLoginNoParam() {
        this.router.navigateByUrl('/login');
    }
    public navTo(url: string, params: NavigationExtras) {
        this.router.navigate([url], params);
    }
    public navProjectHome() {
        this.router.navigate(['/home']);
    }
    public navProjectAnalytics() {

    }
    public navProjectClientsDashboard() {
        this.router.navigate(['home', this.getProjectId(), 'my-client']);
    }
    public navProjectClientsDetail(data: any) {
        this.router.navigate(['home', 'client-detail'], { state: data });
    }
    public navProjectEndpointDashboard() {
        this.router.navigate(['home', this.getProjectId(), 'my-api']);
    }
    public navProjectEndpointDetail(data: any) {
        this.router.navigate(['home', 'endpoint-detail'], { state: data });
    }
    public navProjectCorsDashboard() {

    }
    public navProjectCacheDashboard() {

    }
    public navProjectPermissionsDashboard() {

    }
    public navProjectRolesDashboard() {

    }
    public navProjectRolesDetail(data: any) {
        this.router.navigate(['home', 'role-detail'], { state: data });
    }
    public navProjectUsersDashboard() {
        this.router.navigate(['home', this.getProjectId(), 'my-role']);
    }
    public navProjectAdminDashboard() {

    }
    updateURLQueryParamBeforeSearch(activeRouter: ActivatedRoute, index: number, size: number, query?: string, sortby?: string, sortOrder?: string, key?: string) {
        let params: any = { ...activeRouter.snapshot.queryParams };
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
        const url = this.router.createUrlTree([], { relativeTo: activeRouter, queryParams: params }).toString();
        this.router.navigateByUrl(url);
    }
    updateURLQueryParamPageAndSort(activeRouter: ActivatedRoute, index: number, size: number, sortby?: string, sortOrder?: string) {
        let params: any = { ...activeRouter.snapshot.queryParams };
        let sort = '';
        if (sortby && sortOrder) {
            sort = `by:${sortby},order:${sortOrder}`
        }
        if (sort) {
            params['sort'] = sort;
        }
        params['page'] = `num:${index},size:${size}`;
        const url = this.router.createUrlTree([], { relativeTo: activeRouter, queryParams: params }).toString();
        this.router.navigateByUrl(url);
    }
    public getParams(activeRouter: ActivatedRoute): { page: string, query: string, sort: string, key: string } {
        return {
            page: activeRouter.snapshot.queryParams.page,
            query: activeRouter.snapshot.queryParams.query,
            sort: activeRouter.snapshot.queryParams.sort,
            key: activeRouter.snapshot.queryParams.key
        }
    }
}
