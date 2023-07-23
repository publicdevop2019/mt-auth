import { HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { Utility } from 'src/app/misc/utility';
@Injectable()
export class RequestIdHttpInterceptor implements HttpInterceptor {
    constructor(public dialog: MatDialog) { }
    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<any> {
        req = req.clone({ setHeaders: { "x-mt-request-id": Utility.getChangeId() }});
        return next.handle(req)
    }
}