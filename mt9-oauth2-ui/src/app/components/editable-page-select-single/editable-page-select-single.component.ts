import { Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { EntityCommonService } from 'src/app/clazz/entity.common-service';
import { IIdBasedEntity } from 'src/app/clazz/summary.component';
import { IEditEvent } from '../editable-field/editable-field.component';
export interface IIdName extends IIdBasedEntity {
  name: string
}
@Component({
  selector: 'app-editable-page-select-single',
  templateUrl: './editable-page-select-single.component.html',
  styleUrls: ['./editable-page-select-single.component.css']
})
export class EditablePageSelectSingleComponent implements OnInit {
  @Input() query: string = undefined;
  @ViewChild('ghostRef') set ghostRef(ghostRef: ElementRef) {
    if (ghostRef) { // initially setter gets called with undefined
      this.ref = ghostRef;
      this.observer.observe(this.ref.nativeElement);
    }
  }
  private _visibilityConfig = {
    threshold: 0
  };
  loading: boolean = false;
  ref: ElementRef;
  private pageNumber = 0;
  private pageSize = 5;
  allLoaded = false;
  @Input() inputValue: IOption = undefined;
  @Input() list: IOption[] = [];
  @Input() entitySvc: EntityCommonService<IIdName, IIdName>;
  @Output() newValue: EventEmitter<IEditEvent> = new EventEmitter();
  displayEdit = 'hidden';
  lockEditIcon = false;
  private observer = new IntersectionObserver((entries, self) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        this.loading = true;
        this.entitySvc.readEntityByQuery(this.pageNumber, this.pageSize, undefined, undefined, undefined, { 'loading': false }).subscribe(next => {
          this.loading = false;
          if (next.data.length === 0) {
            this.allLoaded = true;
          } else {
            this.list = [...this.list, ...next.data.map(e => <IOption>{ label: e.name, value: e.id })];
            this.list = this.list.filter((e, index) => {
              return this.list.findIndex((ee) => ee.label === e.label && ee.value === e.value) === index
            });
            if (next.data.length < this.pageSize) {
              this.allLoaded = true;
            } else {
              this.pageNumber++;
            }
          }
        })
      }
    });
  }, this._visibilityConfig);
  constructor() { }

  ngOnInit(): void {
  }
  showEditIcon() {
    this.displayEdit = 'visible'
  }
  hideEditIcon() {
    this.displayEdit = 'hidden'
  }
  doCancel() {
    this.displayEdit = 'hidden';
  }
  doUpdate(newValue: IOption) {
    this.newValue.emit({ original: (this.inputValue && this.inputValue.value as string), next: (newValue.value as string) });
    this.displayEdit = 'hidden';
  }
}
