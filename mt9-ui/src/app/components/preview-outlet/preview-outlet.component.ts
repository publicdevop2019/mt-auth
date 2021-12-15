import { Component, OnInit, ViewChild, ElementRef, Input, AfterContentInit, ChangeDetectorRef } from '@angular/core';
import { IProductDetail } from 'src/app/clazz/validation/aggregate/product/interfaze-product';
@Component({
  selector: 'app-preview-outlet',
  templateUrl: './preview-outlet.component.html',
  styleUrls: ['../../../assets/css/styles.css']
})
export class PreviewOutletComponent implements OnInit, AfterContentInit {
  @Input() productDetai: IProductDetail;
  @ViewChild('productView') productRef: ElementRef;
  @ViewChild('screenW', { static: true }) widthRef: ElementRef;
  constructor(private changeDecRef: ChangeDetectorRef) { }
  ngAfterContentInit(): void {
    this.changeDecRef.detectChanges();//update viewChild
    const popupEl: any = document.createElement('mt-wc-product');
    popupEl.productDetail = this.productDetai;
    popupEl.imgSize = this.widthRef.nativeElement.value+'px';
    this.productRef.nativeElement.appendChild(popupEl);
  }

  ngOnInit() {
  }

}
