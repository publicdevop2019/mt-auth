import { ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { IEndpoint } from 'src/app/clazz/validation/aggregate/endpoint/interfaze-endpoint';
import { EndpointValidator } from 'src/app/clazz/validation/aggregate/endpoint/validator-endpoint';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { MNGMNT_EP_FORM_CONFIG } from 'src/app/form-configs/mngmnt-endpoint.config';
import { CacheService } from 'src/app/services/cache.service';
import { CORSProfileService } from 'src/app/services/cors-profile.service';
import { EndpointService } from 'src/app/services/endpoint.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ClientService } from 'src/app/services/mngmt-client.service';
@Component({
  selector: 'mngmt-app-endpoint',
  templateUrl: './endpoint.component.html',
  styleUrls: ['./endpoint.component.css']
})
export class MngmtEndpointComponent extends Aggregate<MngmtEndpointComponent, IEndpoint> implements OnInit, OnDestroy {
  bottomSheet: IBottomSheet<IEndpoint>;
  constructor(
    public endpointSvc: EndpointService,
    public clientSvc: ClientService,
    public corsSvc: CORSProfileService,
    public cacheSvc: CacheService,
    public httpProxySvc: HttpProxyService,
    fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<MngmtEndpointComponent>,
    cdr: ChangeDetectorRef
  ) {
    super('mngmtApi', JSON.parse(JSON.stringify(MNGMNT_EP_FORM_CONFIG)), new EndpointValidator(), bottomSheetRef, data, fis, cdr)
    this.bottomSheet = data;
    this.fis.formCreated(this.formId)
      .subscribe(next => {
        this.resume()
        if(this.aggregate.method?.toLowerCase()==='get'){
          this.fis.showIfMatch(this.formId, ['cacheProfile'])
        }else{
          this.fis.hideIfMatch(this.formId, ['cacheProfile'])
        }
        if(this.aggregate.websocket){
          this.fis.hideIfMatch(this.formId, ['csrf', 'cors', 'method'])
        }else{
          this.fis.showIfMatch(this.formId, ['csrf', 'cors', 'method'])
        }
        if(this.aggregate.corsProfileId){
          this.fis.showIfMatch(this.formId, ['corsProfile'])
        }else{
          this.fis.hideIfMatch(this.formId, ['corsProfile'])
        }
        this.fis.disableIfNotMatch(this.formId, [])
      })
  }
  ngOnDestroy(): void {
    this.cleanUp()
  }
  resume(): void {
    if (this.aggregate) {
      const var0: Observable<any>[] = [];
      var0.push(this.clientSvc.readEntityByQuery(0, 1, 'id:' + this.aggregate.resourceId))
      if (this.aggregate.corsProfileId) {
        var0.push(this.corsSvc.readEntityByQuery(0, 1, 'id:' + this.aggregate.corsProfileId))
      }
      if (this.aggregate.cacheProfileId) {
        var0.push(this.cacheSvc.readEntityByQuery(0, 1, 'id:' + this.aggregate.cacheProfileId))
      }
      combineLatest(var0).pipe(take(1))
        .subscribe(next => {
          let count = 0;
          this.formInfo.inputs.find(e => e.key === 'resourceId').options = next[count].data.map(e => <IOption>{ label: e.name, value: e.id })
          if (this.aggregate.corsProfileId) {
            count++;
            this.formInfo.inputs.find(e => e.key === 'corsProfile').options = next[count].data.map(e => <IOption>{ label: e.name, value: e.id })
          }
          if (this.aggregate.cacheProfileId) {
            count++;
            this.formInfo.inputs.find(e => e.key === 'cacheProfile').options = next[count].data.map(e => <IOption>{ label: e.name, value: e.id })
          }
          this.fis.restore(this.formId, this.aggregate, true);
          this.fis.formGroupCollection[this.formId].get("secured").setValue(this.aggregate.secured);
          this.fis.formGroupCollection[this.formId].get("csrf").setValue(this.aggregate.csrfEnabled);
          this.fis.formGroupCollection[this.formId].get("isWebsocket").setValue(this.aggregate.websocket ? 'yes' : 'no');
          if (this.aggregate.corsProfileId) {
            this.fis.formGroupCollection[this.formId].get("cors").setValue(true);
            this.fis.formGroupCollection[this.formId].get("corsProfile").setValue(this.aggregate.corsProfileId);
          }
          if (this.aggregate.cacheProfileId) {
            this.fis.formGroupCollection[this.formId].get("cacheProfile").setValue(this.aggregate.cacheProfileId);
          }
          this.cdr.markForCheck()
        })

    }
  }
  ngOnInit() {
  }
  convertToPayload(cmpt: MngmtEndpointComponent): IEndpoint {
    throw new Error('Method not implemented.');
  }
  update() {
    throw new Error('Method not implemented.');
  }
  create() {
    throw new Error('Method not implemented.');
  }
  errorMapper(original: ErrorMessage[], cmpt: MngmtEndpointComponent): ErrorMessage[] {
    throw new Error('Method not implemented.');
  }
}

