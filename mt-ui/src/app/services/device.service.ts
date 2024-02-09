import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Subject } from 'rxjs';
import { RouterWrapperService } from './router-wrapper';
@Injectable({
    providedIn: 'root'
})
export class DeviceService {
    refreshSummary: Subject<any> = new Subject();
    public operationCancelled = new Subject();
    public overlayData:any;
    constructor(
        public httpClient: HttpClient,
        private router: RouterWrapperService,
    ) {
    }
}