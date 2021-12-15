import { HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import * as UUID from 'uuid/v1';
@Injectable()
export class RequestIdHttpInterceptor implements HttpInterceptor {
    constructor(public dialog: MatDialog) { }
    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<any> {
        req = req.clone({ setHeaders: { UUID: UUID() }});
        return next.handle(req)
    }
}