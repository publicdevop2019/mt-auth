import { Component, OnInit, Input, HostBinding, HostListener, Output, EventEmitter, ViewChild, ElementRef, ChangeDetectorRef } from '@angular/core';
export interface IEditEvent {
  original: string,
  next: string
}
@Component({
  selector: 'app-editable-field',
  templateUrl: './editable-field.component.html',
  styleUrls: ['./editable-field.component.css']
})
export class EditableFieldComponent implements OnInit {
  @Input() inputValue: string = '';
  @Output() newValue: EventEmitter<IEditEvent> = new EventEmitter();
  @ViewChild("inputField") inputField: ElementRef<HTMLInputElement>;
  displayEdit = 'hidden';
  editView = false;
  constructor(private cdr:ChangeDetectorRef) {
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
  doUpdate(newValue: string) {
    this.newValue.emit({ original: this.inputValue, next: newValue });
    this.displayEdit = 'hidden';
    this.editView = false;
  }
  doEdit() {
    this.editView = true;
    this.cdr.detectChanges();
    this.inputField.nativeElement.focus();
  }
}
