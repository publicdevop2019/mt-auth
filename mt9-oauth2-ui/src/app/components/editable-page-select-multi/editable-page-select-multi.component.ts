import { Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { EntityCommonService } from 'src/app/clazz/entity.common-service';
import { IIdName } from '../editable-page-select-single/editable-page-select-single.component';
import { IEditListEvent } from '../editable-select-multi/editable-select-multi.component';

@Component({
  selector: 'app-editable-page-select-multi',
  templateUrl: './editable-page-select-multi.component.html',
  styleUrls: ['./editable-page-select-multi.component.css']
})
export class EditablePageSelectMultiComponent implements OnInit {
  @Input() query: string = undefined;
  @Input() inputOptions: IOption[] = [];
  @Input() list: IOption[] = [];
  @Input() readonly: boolean = false;
  @Output() newValue: EventEmitter<IEditListEvent> = new EventEmitter();
  @Input() entitySvc: EntityCommonService<IIdName, IIdName>;
  private _visibilityConfig = {
    threshold: 0
  };
  loading: boolean = false;
  ref: ElementRef;
  private pageNumber = 0;
  private pageSize = 5;
  allLoaded = false;
  private observer = new IntersectionObserver((entries, self) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        this.loading = true;
        this.entitySvc.readEntityByQuery(this.pageNumber, this.pageSize, this.query, undefined, undefined, { 'loading': false }).subscribe(next => {
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
  @ViewChild('ghostRef') set ghostRef(ghostRef: ElementRef) {
    if (ghostRef) { // initially setter gets called with undefined
      this.ref = ghostRef;
      this.observer.observe(this.ref.nativeElement);
    }
  }
  inputOptionsNext: IOption[] = [];
  displayEdit = 'hidden';
  editView = false;
  constructor() {
  }
  ngOnInit() {
  }
  showEditIcon() {
    this.displayEdit = 'visible'
  }
  hideEditIcon() {
    this.displayEdit = 'hidden'
  }
  doCancel() {
    this.displayEdit = 'hidden';
    this.editView = false;
  }
  doUpdate() {
    this.newValue.emit({ original: this.inputOptions, next: this.inputOptionsNext });
    this.displayEdit = 'hidden';
    this.editView = false;
  }
  selected(e: IOption): void {
    this.inputOptionsNext.push(e);
  }
  doEdit() {
    this.editView = true;
    this.inputOptionsNext = JSON.parse(JSON.stringify(this.inputOptions))
  }
  remove(item: string): void {
    this.inputOptionsNext = this.inputOptionsNext.filter(e => e.label !== item)
  }
  getLabel(inputs: IOption[]) {
    return inputs.map(e => e.label)
  }
  removeFirst(input: string[]) {
    return input.filter((e, i) => i !== 0);
  }
}
