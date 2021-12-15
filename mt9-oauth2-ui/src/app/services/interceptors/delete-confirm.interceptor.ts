import { HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { filter, switchMap } from 'rxjs/operators';
import { OperationConfirmDialogComponent } from '../../components/operation-confirm-dialog/operation-confirm-dialog.component';
@Injectable()
export class DeleteConfirmHttpInterceptor implements HttpInterceptor {
    constructor(public dialog: MatDialog) { }
    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<any> {
        if (req.method.toLowerCase() === 'delete') {
            const dialogRef = this.dialog.open(OperationConfirmDialogComponent);
            return dialogRef.afterClosed().pipe(filter(result => result), switchMap(() => next.handle(req)));
        } else {
            return next.handle(req)
        }
    }
}