import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { FormInfoService } from 'mt-form-builder';
import { IForm, IOption } from 'mt-form-builder/lib/classes/template.interface';
import { Observable } from 'rxjs';
import { FORM_TABLE_COLUMN_CONFIG } from 'src/app/form-configs/table-column.config';
@Component({
  selector: 'app-table-column-config',
  templateUrl: './table-column-config.component.html',
  styleUrls: ['./table-column-config.component.css']
})
export class TableColumnConfigComponent implements OnInit, OnChanges {
  @Input() columns: { label: string, value: string }[] = [];
  @Input() formId: string = '';
  formInfo: IForm = JSON.parse(JSON.stringify(FORM_TABLE_COLUMN_CONFIG));
  showOverlay = false
  public static keyName = 'displayColumns';
  private formCreatedOb: Observable<string>;
  constructor(
    public fis: FormInfoService,
  ) {
  }
  ngOnChanges(changes: SimpleChanges): void {
    if (JSON.stringify(changes.columns.currentValue) !== JSON.stringify(changes.columns.previousValue)) {
      this.formInfo.inputs[0].options = this.columns.map(e => (e) as IOption)
    }
  }
  ngOnInit(): void {
    const ctrl = new FormControl(this.columns.map(e => e.value))
    this.fis.formGroupCollection[this.formId] = new FormGroup({ 'displayColumns': ctrl })
    this.formInfo.inputs[0].options = this.columns.map(e => (e) as IOption)
  }
  handleClick($event: Event) {
    $event.stopPropagation()//prevent menu from close
  }
}
