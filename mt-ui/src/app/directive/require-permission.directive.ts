import { Directive, Input, TemplateRef, ViewContainerRef } from '@angular/core';
import { HttpProxyService } from '../services/http-proxy.service';

@Directive({
    selector: '[appRequireP]'
})
export class RequirePermissionDirective {
    private hasView = false;
    @Input() set appRequireP(permissionId: string[]) {
        const p = (permissionId || []).filter(e => e);
        let shouldShow: boolean;
        if (p.length === 0) {
            shouldShow = false
        } else {
            shouldShow = p.filter(e => this.httpSvc.currentUserAuthInfo.permissionIds.includes(e)).length > 0
        }
        if (shouldShow && !this.hasView) {
            this.viewContainer.createEmbeddedView(this.templateRef);
            this.hasView = true;
        } else if (!shouldShow && this.hasView) {
            this.viewContainer.clear();
            this.hasView = false;
        }
    }
    constructor(
        private templateRef: TemplateRef<any>,
        private viewContainer: ViewContainerRef,
        private httpSvc: HttpProxyService
    ) {
    }

}
