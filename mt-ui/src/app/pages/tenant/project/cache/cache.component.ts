import { Component } from '@angular/core';
import { Utility } from 'src/app/misc/utility';
import { Validator } from 'src/app/misc/validator';
import { ICacheProfile } from 'src/app/misc/interface';
import { FormGroup, FormControl } from '@angular/forms';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { RESOURCE_NAME } from 'src/app/misc/constant';
@Component({
  selector: 'app-cache',
  templateUrl: './cache.component.html',
  styleUrls: ['./cache.component.css']
})
export class CacheComponent {
  nameErrorMsg: string = undefined;
  allowCacheErrorMsg: string = undefined;
  private projectId = this.router.getProjectIdFromUrl()
  private cacheUrl = Utility.getProjectResource(this.projectId, RESOURCE_NAME.CACHE)
  fg = new FormGroup({
    id: new FormControl({ value: '', disabled: true }),
    name: new FormControl(''),
    description: new FormControl(''),
    allowCache: new FormControl(''),
    cacheControl: new FormControl(''),
    maxAgeValue: new FormControl(''),
    smaxAgeValue: new FormControl(''),
    vary: new FormControl(''),
    expires: new FormControl(''),
    etagValidation: new FormControl({ value: false, disabled: false }),
    etagType: new FormControl(''),
  });

  headerFg = new FormGroup({
    mustRevalidate: new FormControl({ value: false, disabled: false }),
    noCache: new FormControl({ value: false, disabled: false }),
    noStore: new FormControl({ value: false, disabled: false }),
    noTransform: new FormControl({ value: false, disabled: false }),
    public: new FormControl({ value: false, disabled: false }),
    private: new FormControl({ value: false, disabled: false }),
    proxyRevalidate: new FormControl({ value: false, disabled: false }),
    maxAge: new FormControl({ value: false, disabled: false }),
    sMaxage: new FormControl({ value: false, disabled: false }),
  });
  data: ICacheProfile = undefined;
  context: 'NEW' | 'EDIT' = 'NEW';
  allowError: boolean = false;
  changeId: string = Utility.getChangeId();

  constructor(
    public httpSvc: HttpProxyService,
    public router: RouterWrapperService,
  ) {
    const configId = this.router.getCacheConfigIdFromUrl();
    if (configId === 'template') {
    } else {
      this.context = 'EDIT'
      if (this.router.getData() === undefined) {
        this.router.navProjectHome()
      }
      this.data = this.router.getData() as ICacheProfile;
      this.fg.get('id').setValue(this.data.id)
      this.fg.get('name').setValue(this.data.name)
      this.fg.get('description').setValue(this.data.description ? this.data.description : '')
      this.fg.get('allowCache').setValue(this.data.allowCache ? 'yes' : 'no')
      this.fg.get('cacheControl').setValue(this.data.cacheControl)
      this.fg.get('maxAgeValue').setValue(this.data.maxAge || '')
      this.fg.get('smaxAgeValue').setValue(this.data.smaxAge || '')
      this.fg.get('etagValidation').setValue(this.data.etag)
      this.fg.get('etagType').setValue(this.data.weakValidation)
      this.fg.get('expires').setValue(this.data.expires ? this.data.expires : '')
      this.fg.get('vary').setValue(this.data.vary ? this.data.vary : '')
    }
    this.fg.valueChanges.subscribe(() => {
      if (this.allowError) {
        this.validateForm()
      }
    })
    this.headerFg.valueChanges.subscribe(() => {
      if (this.allowError) {
        this.validateForm()
      }
    })
  }

  update() {
    this.allowError = true
    if (this.validateForm()) {
      this.httpSvc.updateEntity(this.cacheUrl, this.data.id, this.convertToPayload(), this.changeId)
    }
  }
  create() {
    this.allowError = true
    if (this.validateForm()) {
      this.httpSvc.createEntity(this.cacheUrl, this.convertToPayload(), this.changeId)
    }
  }

  private convertToPayload(): ICacheProfile {
    let formGroup = this.fg;
    let cacheControls = (formGroup.get('cacheControl').value as string[])
    let allowCache = formGroup.get('allowCache').value === 'yes';
    return {
      id: formGroup.get('id').value,//value is ignored
      name: formGroup.get('name').value,
      description: formGroup.get('description').value ? formGroup.get('description').value : null,
      allowCache: formGroup.get('allowCache').value === 'yes',
      cacheControl: cacheControls.length > 0 ? cacheControls : null,
      expires: formGroup.get('expires').value ? (+formGroup.get('expires').value) : null,
      maxAge: formGroup.get('maxAgeValue').value ? (+formGroup.get('maxAgeValue').value) : null,
      smaxAge: formGroup.get('smaxAgeValue').value ? (+formGroup.get('smaxAgeValue').value) : null,
      vary: formGroup.get('vary').value ? formGroup.get('vary').value : null,
      etag: allowCache ? formGroup.get('etagValidation').value : null,
      weakValidation: allowCache ? formGroup.get('etagType').value : null,
      version: this.data && this.data.version
    }
  }
  private validateForm() {
    const fg = this.fg;
    const var0 = Validator.exist(fg.get('name').value)
    this.nameErrorMsg = var0.errorMsg

    const var1 = Validator.exist(fg.get('allowCache').value)
    this.allowCacheErrorMsg = var1.errorMsg
    return !var0.errorMsg && !var1.errorMsg
  }

}
