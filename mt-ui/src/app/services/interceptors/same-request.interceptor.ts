import { HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { filter, switchMap } from 'rxjs/operators';
import { OperationConfirmDialogComponent } from '../../components/operation-confirm-dialog/operation-confirm-dialog.component';
import { Utility } from 'src/app/misc/utility';
import { DeviceService } from '../device.service';
@Injectable()
export class SameRequestHttpInterceptor implements HttpInterceptor {
    constructor(public dialog: MatDialog, public dvs: DeviceService) { }
    private urlMap = new Map<string, string>()
    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<any> {
        if (['post', 'put', 'patch'].includes(req.method.toLowerCase()) && !req.url.includes('/oauth/token')) {
            let storedChangeId = this.urlMap.get(req.method + '_' + req.urlWithParams);
            if (req.headers.get('changeId') === storedChangeId) {
                const dialogRef = this.dialog.open(OperationConfirmDialogComponent);
                return dialogRef.afterClosed().pipe(filter(result => {
                    if (result === false) {
                        this.dvs.operationCancelled.next()
                    }
                    return result
                }), switchMap(() => {
                    req = req.clone({ setHeaders: { changeId: Utility.getChangeId() } });
                    return next.handle(req)
                }));
            } else {
                this.urlMap.set(req.method + '_' + req.urlWithParams, req.headers.get('changeId'))
                return next.handle(req)
            }
        } else {
            return next.handle(req)
        }
    }
}