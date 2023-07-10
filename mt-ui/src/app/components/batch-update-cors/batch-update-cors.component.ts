import { Component, Inject, OnDestroy } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { FormInfoService } from "mt-form-builder";
import { IQueryProvider } from "mt-form-builder/lib/classes/template.interface";
import { IEndpoint } from "src/app/clazz/validation/endpoint.interface";
import { FORM_CONFIG } from "src/app/form-configs/batch-operation.config";
import { MyCacheService } from "src/app/services/my-cache.service";
import { MyCorsProfileService } from "src/app/services/my-cors-profile.service";
import { DeviceService } from "src/app/services/device.service";
import { EndpointService } from "src/app/services/endpoint.service";
import { HttpProxyService } from "src/app/services/http-proxy.service";
import * as UUID from 'uuid/v1';
export interface DialogData {
    data: { id: string, description: string }[]
}
type ISTATUS = 'PENDING' | 'SUCCESS' | 'WIP' | 'FAILED';
@Component({
    selector: 'batch-update-cors',
    templateUrl: 'batch-update-cors.component.html',
})
export class BatchUpdateCorsComponent implements OnDestroy {
    formId: string = 'batch-operation-form';
    displayedColumns: string[] = ['id', 'status'];
    dataSource: MatTableDataSource<{ id: string; status: ISTATUS }>;
    batchJobConfirmed: boolean;
    constructor(
        public dialogRef: MatDialogRef<BatchUpdateCorsComponent>,
        private corsSvc: MyCorsProfileService,
        private cacheSvc: MyCacheService,
        private endpointSvc: EndpointService,
        private httpProxySvc: HttpProxyService,
        private deviceSvc: DeviceService,
        @Inject(MAT_DIALOG_DATA) public data: DialogData,
        private fis: FormInfoService
    ) {
        this.fis.queryProvider[this.formId + '_' + 'corsId'] = this.getCorsProfiles();
        this.fis.queryProvider[this.formId + '_' + 'cacheId'] = this.getCacehProfiles();
        this.fis.init(FORM_CONFIG,this.formId);
        this.fis.formGroups[this.formId].get('type').valueChanges.subscribe(next => {
            if (next === 'cors') {
                this.fis.showIfMatch(this.formId, ['corsId'])
                this.fis.hideIfNotMatch(this.formId, ['corsId', 'type'])
            }
            if (next === 'cache') {
                this.fis.showIfMatch(this.formId, ['cacheId'])
                this.fis.hideIfNotMatch(this.formId, ['cacheId', 'type'])
            }
        })
    }
    ngOnDestroy(): void {
        this.fis.reset(this.formId);
    }
    getCorsProfiles() {
        return {
            readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
                return this.httpProxySvc.readEntityByQuery(this.corsSvc.entityRepo, num, size, "", by, order, header)
            }
        } as IQueryProvider
    }
    getCacehProfiles() {
        return {
            readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
                return this.httpProxySvc.readEntityByQuery(this.cacheSvc.entityRepo, num, size, "", by, order, header)
            }
        } as IQueryProvider
    }
    onNoClick(): void {
        this.dialogRef.close();
    }
    getIds() {
        return this.data.data.map(e => e.id).join(', ')
    }
    startBatchJob() {
        this.batchJobConfirmed = true;
        const var0 = this.data.data.map(e => ({ id: e.id, status: 'PENDING' as ISTATUS }))
        this.dataSource = new MatTableDataSource(var0);
        var0.forEach(endpoint => {
            this.dataSource.data.find(e => e.id === endpoint.id).status = 'WIP'
            this.httpProxySvc.readEntityById<IEndpoint>(this.endpointSvc.entityRepo,endpoint.id,{ 'loading': false }).subscribe(next => {
                const var0=this.fis.formGroups[this.formId].get('type').value
                if( var0==='cors'){
                    next.corsProfileId=this.fis.formGroups[this.formId].get('corsId').value
                }else if(var0==='cache'){
                    next.cacheProfileId=this.fis.formGroups[this.formId].get('cacheId').value
                }else{
                    // will not reach
                }
                this.httpProxySvc.updateEntityExt(this.endpointSvc.entityRepo, endpoint.id, next, UUID()).subscribe(next => {
                    this.dataSource.data.find(e => e.id === endpoint.id).status = 'SUCCESS'
                },()=>{
                    this.dataSource.data.find(e => e.id === endpoint.id).status = 'FAILED'
                })
            })
        })

    }
    close() {
        this.dialogRef.close();
        this.deviceSvc.refreshSummary.next();
    }
}