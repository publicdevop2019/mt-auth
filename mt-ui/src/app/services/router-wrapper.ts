import { Injectable } from '@angular/core';
import { ActivatedRoute, NavigationExtras, Router } from '@angular/router';
import { IEndpoint } from '../misc/interface';
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
        return this.router.getCurrentNavigation().extras?.state
    }
    public getParam() {
        return this.router.routerState.snapshot.root.queryParams;
    }
    public getProjectIdFromUrl() {
        return this.stripeEndingQuery(this.router.url.split('/')[2]);
    }
    public getRoleIdFromUrl() {
        return this.stripeEndingQuery(this.router.url.split('/')[4]);
    }
    public getUserIdFromUrl() {
        return this.stripeEndingQuery(this.router.url.split('/')[4]);
    }
    public getClientIdFromUrl() {
        return this.stripeEndingQuery(this.router.url.split('/')[4]);
    }
    public getCacheConfigIdFromUrl() {
        return this.stripeEndingQuery(this.router.url.split('/')[4]);
    }
    public getCorsConfigIdFromUrl() {
        return this.stripeEndingQuery(this.router.url.split('/')[4]);
    }
    public getEndpointIdFromUrl() {
        return this.stripeEndingQuery(this.router.url.split('/')[4]);
    }
    public getMgmtEndpointIdFromUrl() {
        return this.stripeEndingQuery(this.router.url.split('/')[3]);
    }
    public getMgmtClientIdFromUrl() {
        return this.stripeEndingQuery(this.router.url.split('/')[3]);
    }
    public getSubRequestIdFromUrl() {
        return this.stripeEndingQuery(this.router.url.split('/')[2]);
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

    public navProjectCacheConfigsDashboard() {
        this.router.navigate(['projects', this.getProjectIdFromUrl(), 'cache-configs']);
    }
    public navSubRequestDashboard() {
        this.router.navigate(['requests']);
    }
    public navApiMarket() {
        this.router.navigate(['market']);
    }
    public navMarket() {
        this.router.navigate(['market']);
    }
    public navProjectCacheConfigsDetail(id: string, data: any) {
        this.router.navigate(['projects', this.getProjectIdFromUrl(), 'cache-configs', id], { state: data });
    }
    public navSubscribeRequestDetail(id: string, data: any) {
        this.router.navigate(['requests', id], { state: data });
    }
    public navNewSubscribeRequestDetail(data: IEndpoint) {
        this.router.navigate(['requests', 'template'], { state: data });
    }
    public navProjectNewCacheConfigsDetail() {
        this.router.navigate(['projects', this.getProjectIdFromUrl(), 'cache-configs', 'template']);
    }

    public navProjectCorsConfigsDashboard() {
        this.router.navigate(['projects', this.getProjectIdFromUrl(), 'cors-configs']);
    }
    public navProjectUserDashboard() {
        this.router.navigate(['projects', this.getProjectIdFromUrl(), 'users']);
    }
    public navProjectCorsConfigsDetail(id: string, data: any) {
        this.router.navigate(['projects', this.getProjectIdFromUrl(), 'cors-configs', id], { state: data });
    }
    public navProjectNewCorsConfigsDetail() {
        this.router.navigate(['projects', this.getProjectIdFromUrl(), 'cors-configs', 'template']);
    }

    public navProjectNewClientsDetail(data: any) {
        this.router.navigate(['projects', this.getProjectIdFromUrl(), 'clients', 'template'], { state: data });
    }
    public navProjectEndpointDashboard() {
        this.router.navigate(['projects', this.getProjectIdFromUrl(), 'endpoints']);
    }
    public navMgmtEndpointDashboard() {
        this.router.navigate(['mgmt', 'endpoints']);
    }
    public navMgmtClientDashboard() {
        this.router.navigate(['mgmt', 'clients']);
    }
    public navProjectEndpointDetail(id: string) {
        this.router.navigate(['projects', this.getProjectIdFromUrl(), 'endpoints', id]);
    }
    public navMgmtEndpointDetail(id: string) {
        this.router.navigate(['mgmt', 'endpoints', id]);
    }
    public navMgmtClientDetail(id: string) {
        this.router.navigate(['mgmt', 'clients', id]);
    }
    public navProjectNewEndpointDetail(data: any) {
        this.router.navigate(['projects', this.getProjectIdFromUrl(), 'endpoints', 'template'], { state: data });
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
    public navProjectUserDetail(id: string) {
        this.router.navigate(['projects', this.getProjectIdFromUrl(), 'users', id]);
    }
    public navMgmtUserDetail(id: string) {
        this.router.navigate(['mgmt', 'users', id]);
    }
    public navMgmtUserDashboard() {
        this.router.navigate(['mgmt', 'users']);
    }
    public navProjectUsersDashboard() {
        this.router.navigate([RouterWrapperService.HOME_URL, this.getProjectIdFromUrl(), 'my-role']);
    }
    public navProjectAdminDashboard() {

    }
    public updateURLQueryParamBeforeSearch(activeRouter: ActivatedRoute, index: number, size: number, query?: string, sortby?: string, sortOrder?: string, key?: string) {
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
    public updateURLQueryParamPageAndSort(activeRouter: ActivatedRoute, index: number, size: number, sortby?: string, sortOrder?: string) {
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
    private stripeEndingQuery(idWithQuery: string) {
        if (idWithQuery.includes('?')) {
            return idWithQuery.split('?')[0]
        } else {
            return idWithQuery;
        }
    }
}
