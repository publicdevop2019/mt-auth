import { Injectable } from '@angular/core';
import { ActivatedRoute, NavigationExtras, Router } from '@angular/router';
import { Logger } from '../misc/logger';
@Injectable({
    providedIn: 'root'
})
export class RouterWrapperService {
    public static HOME_URL = 'welcome'
    public static AUTHORIZE_URL = 'authorize'
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
    public getProjectIdFromUrl() {
        return this.router.url.split('/')[2];
    }
    public getRoleIdFromUrl() {
        return this.router.url.split('/')[4];
    }
    public getClientIdFromUrl() {
        return this.router.url.split('/')[4];
    }
    public getEndpointIdFromUrl() {
        return this.router.url.split('/')[4];
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
        this.router.navigate(['/' + RouterWrapperService.HOME_URL]);
    }
    public navProjectAnalytics() {

    }
    public navClientsDashboard(projectId: string) {
        this.router.navigate(['projects', projectId, 'clients']);
    }
    public navEndpointDashboard(projectId: string) {
        this.router.navigate(['projects', projectId, 'endpoints']);
    }
    public navProjectClientsDashboard() {
        this.router.navigate(['projects', this.getProjectIdFromUrl(), 'clients']);
    }
    public navProjectClientsDetail(id: string) {
        this.router.navigate(['projects', this.getProjectIdFromUrl(), 'clients', id]);
    }
    public navProjectNewClientsDetail(data: any) {
        this.router.navigate(['projects', this.getProjectIdFromUrl(), 'clients', 'template'], { state: data });
    }
    public navProjectEndpointDashboard() {
        this.router.navigate(['projects', this.getProjectIdFromUrl(), 'endpoints']);
    }
    public navProjectEndpointDetail(id: string) {
        this.router.navigate(['projects', this.getProjectIdFromUrl(), 'endpoints',id]);
    }
    public navProjectNewEndpointDetail(data: any) {
        this.router.navigate(['projects', this.getProjectIdFromUrl(), 'endpoints','template'], { state: data });
    }
    public navProjectCorsDashboard() {

    }
    public navProjectCacheDashboard() {

    }
    public navProjectPermissionsDashboard() {

    }
    public navProjectRolesDashboard() {
        this.router.navigate(['projects', this.getProjectIdFromUrl(), 'roles']);
    }
    public navProjectRolesDetail(id: string) {
        this.router.navigate(['projects', this.getProjectIdFromUrl(), 'roles', id]);
    }
    public navProjectUsersDashboard() {
        this.router.navigate([RouterWrapperService.HOME_URL, this.getProjectIdFromUrl(), 'my-role']);
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
