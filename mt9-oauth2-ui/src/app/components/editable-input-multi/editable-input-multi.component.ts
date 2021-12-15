import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Component, EventEmitter, Input, OnInit, Output, ViewChild, ElementRef } from '@angular/core';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { MatChipInputEvent } from '@angular/material/chips';
export interface IEditInputListEvent {
  original: string[],
  next: string[]
}
@Component({
  selector: 'app-editable-input-multi',
  templateUrl: './editable-input-multi.component.html',
  styleUrls: ['./editable-input-multi.component.css']
})
export class EditableInputMultiComponent implements OnInit {

  @Input() inputOptions: string[] = [];
  @Output() newValue: EventEmitter<IEditInputListEvent> = new EventEmitter();
  @ViewChild("userInput") userInput: ElementRef<HTMLInputElement>;
  inputOptionsNext: string[] = [];
  displayEdit = 'hidden';
  editView = false;
  separatorKeysCodes: number[] = [ENTER, COMMA];
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
  add(e: MatChipInputEvent): void {
    if (e.value) {
      this.inputOptionsNext.push(e.value);
      this.userInput.nativeElement.value = ''
    }
  }
  doEdit() {
    this.editView = true;
    this.inputOptionsNext = JSON.parse(JSON.stringify(this.inputOptions))
  }
  remove(item: string): void {
    this.inputOptionsNext = this.inputOptionsNext.filter(e => e !== item)
  }
  getLabel(inputs: IOption[]) {
    return inputs.map(e => e.label)
  }

}
