import { Component, ElementRef, ViewChild, ChangeDetectorRef, Input } from "@angular/core";
import { FormGroup } from "@angular/forms";
import { IOption, IQueryProvider } from "src/app/misc/interface";

@Component({
  selector: 'app-paginated-select',
  templateUrl: './paginated-select.component.html',
  styleUrls: ['./paginated-select.component.css']
})
export class PaginatedSelectComponent {
  private _visibilityConfig = {
    threshold: 0
  };
  loading: boolean = false;
  allLoaded: boolean = false;
  ref: ElementRef;
  @Input() loadElements: IQueryProvider;
  @Input() label: string;
  @Input() fg: FormGroup;
  @Input() key: string;
  @Input() multiple: boolean = false;
  @Input() options: IOption[] = [];
  private pageNumber = 0;
  private pageSize = 10;
  @ViewChild('ghostRef') set ghostRef(ghostRef: ElementRef) {
    if (ghostRef) { // initially setter gets called with undefined
      this.ref = ghostRef;
      this.observer.observe(this.ref.nativeElement);
    }
  }
  private observer = new IntersectionObserver((entries, self) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        this.loading = true;
        this.loadElements
          .readByQuery(this.pageNumber, this.pageSize, undefined, undefined, undefined, { 'loading': false }).subscribe(next => {
            this.loading = false;
            if (next.data.length === 0) {
              this.allLoaded = true;
            } else {
              const nextOptions = [...this.options, ...next.data.map(e => <IOption>{ label: e.name, value: e.id })];
              this.updateOption(nextOptions);
              //NOTE cannot use fis.updateOption due to it will close dropdown
              // this.fis.updateOption(this.formId, this.key, nextOptions)
              if (next.data.length < this.pageSize) {
                this.allLoaded = true;
              } else {
                this.pageNumber++;
              }
            }
            //NOTE required to refresh dropdown
            this.cdRef.markForCheck();
          })
      }
    });
  }, this._visibilityConfig);
  constructor(
    public cdRef: ChangeDetectorRef,
  ) {
  }

  /**
   * update option to avoid duplicate values during resume.
   * @param options update option
   */
  private updateOption(options: IOption[]) {
    const duplicateObj = options.filter((e, i) => options.findIndex(ee => ee.value === e.value) !== i);
    const duplicateKey = Array.from(new Set(duplicateObj.map(e => e.value)));
    const withoutDuplicate = options.filter(e => !duplicateKey.includes(e.value));
    const onlyDuplicate = options.filter(e => duplicateKey.includes(e.value));
    const var0 = [];
    duplicateKey.forEach(key => {
      const same = onlyDuplicate.filter(e => e.value === key);
      let select: IOption = undefined;
      same.forEach(e => {
        if (!select) {
          select = e;
        } else {
          if (e.label.length > select.label.length) {
            select = e;
          }
        }
      })
      var0.push(select)
    })
    const next = [...var0, ...withoutDuplicate]
    this.options = next;
  }
}
