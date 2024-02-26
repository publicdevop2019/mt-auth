import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
@Injectable({
    providedIn: 'root'
})
export class DeviceService {
    refreshSummary: Subject<any> = new Subject();
    public operationCancelled = new Subject();
    public overlayData: any;
}