import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
export interface IEditListEvent {
  original: IOption[],
  next: IOption[]
}
@Component({
  selector: 'app-editable-select-multi',
  templateUrl: './editable-select-multi.component.html',
  styleUrls: ['./editable-select-multi.component.css']
})
export class EditableSelectMultiComponent implements OnInit {
  @Input() inputOptions: IOption[] = [];
  @Input() list: IOption[] = [];
  @Input() readonly: boolean = false;
  @Output() newValue: EventEmitter<IEditListEvent> = new EventEmitter();
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
}
