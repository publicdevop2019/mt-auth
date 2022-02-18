import { IEditBooleanEvent } from '../components/editable-boolean/editable-boolean.component';
import { IEditEvent } from '../components/editable-field/editable-field.component';
import { IEditInputListEvent } from '../components/editable-input-multi/editable-input-multi.component';
import { IEditListEvent } from '../components/editable-select-multi/editable-select-multi.component';
import { DeviceService } from '../services/device.service';
import { HttpProxyService } from '../services/http-proxy.service';
import { CustomHttpInterceptor } from '../services/interceptors/http.interceptor';
import { IEntityService, IIdBasedEntity } from "./summary.component";

export class EntityCommonService<C extends IIdBasedEntity, D> implements IEntityService<C, D>{
    httpProxySvc: HttpProxyService;
    pageNumber: number = 0;
    entityRepo: string;
    interceptor: CustomHttpInterceptor
    deviceSvc: DeviceService
    constructor(httpProxySvc: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
        this.httpProxySvc = httpProxySvc;
        this.interceptor = interceptor;
        this.deviceSvc = deviceSvc;
    }
    readById(id: string) {
        return this.httpProxySvc.readEntityById<D>(this.entityRepo, id)
    };
    readEntityByQuery(num: number, size: number, query?: string, by?: string, order?: string,headers?:{}) {
        return this.httpProxySvc.readEntityByQuery<C>(this.entityRepo, num, size, query, by, order,headers)
    };
    deleteByQuery(query: string, changeId: string) {
        this.httpProxySvc.deleteEntityByQuery(this.entityRepo, query, changeId).subscribe(next => {
            this.notify(next)
            this.refreshPage()
        })
    };
    deleteById(id: string, changeId: string) {
        this.httpProxySvc.deleteEntityById(this.entityRepo, id, changeId).subscribe(next => {
            this.notify(!!next)
            this.refreshPage()
        })
    };
    create(s: D, changeId: string) {
        this.httpProxySvc.createEntity(this.entityRepo, s, changeId).subscribe(next => {
            this.notify(!!next)
            this.refreshPage()
        });
    };
    update(id: string, s: D, changeId: string) {
        this.httpProxySvc.updateEntity(this.entityRepo, id, s, changeId).subscribe(next => {
            this.notify(!!next)
            this.refreshPage()
        })
    };
    patch(id: string, event: IEditEvent, changeId: string, fieldName: string) {
        this.httpProxySvc.patchEntityById(this.entityRepo, id, fieldName, event, changeId).subscribe(next => {
            this.notify(next)
            this.refreshPage()
        })
    };
    patchAtomicNum(id: string, event: IEditEvent, changeId: string, fieldName: string) {
        this.httpProxySvc.patchEntityAtomicById(this.entityRepo, id, fieldName, event, changeId).subscribe(next => {
            this.notify(next)
            this.refreshPage()
        })
    };
    patchList(id: string, event: IEditListEvent, changeId: string, fieldName: string) {
        this.httpProxySvc.patchEntityListById(this.entityRepo, id, fieldName, event, changeId).subscribe(next => {
            this.notify(next)
            this.refreshPage()
        })

    };
    patchMultiInput(id: string, event: IEditInputListEvent, changeId: string, fieldName: string) {
        this.httpProxySvc.patchEntityInputListById(this.entityRepo, id, fieldName, event, changeId).subscribe(next => {
            this.notify(next)
            this.refreshPage()
        })

    };
    patchBoolean(id: string, event: IEditBooleanEvent, changeId: string, fieldName: string) {
        this.httpProxySvc.patchEntityBooleanById(this.entityRepo, id, fieldName, event, changeId).subscribe(next => {
            this.notify(next)
            this.refreshPage()    
        })

    };
    notify(result: boolean) {
        result ? this.interceptor.openSnackbar('OPERATION_SUCCESS') : this.interceptor.openSnackbar('OPERATION_FAILED');
    }
    refreshPage(){
        this.deviceSvc.refreshSummary.next();
    }
}