import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Component, ComponentFactoryResolver, ElementRef, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges, ViewChild, ViewContainerRef } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { interval, Observable, Subscription } from 'rxjs';
import { debounce, filter } from 'rxjs/operators';
import { DeviceService } from 'src/app/services/device.service';
export interface ISearchEvent {
  value: string,
  key?: string,//see below
  resetPage: boolean
}
export interface ISearchConfig {
  searchValue: string,
  searchLabel: string,
  key?: string,//to resolve catalog and attributes when resume unable to know which one to resume
  type: 'text' | 'dropdown' | 'boolean' | 'range' | 'custom'
  multiple?: {
    delimiter: '$' | '.'
  }
  source?: IOption[],
  component?: any
  componentInputMap?: { [key: string]: any }
  componentOutputMap?: { [key: string]: any }
  resumeFromUrl?: (searchCmpt: SearchComponent, queryValue: string) => void
}
@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnDestroy, OnInit, OnChanges {
  @Input() searchConfigs: ISearchConfig[] = [];
  @Output() search: EventEmitter<ISearchEvent> = new EventEmitter()
  filteredList: Observable<IOption[]>;
  searchItems: IOption[] = [];
  selectedItem = new FormControl();
  options: FormGroup;
  searchLevel1 = new FormControl();
  searchQuery = new FormControl({ value: [] as string[] });
  searchByString = new FormControl();
  searchByNumMin = new FormControl();
  searchByNumMax = new FormControl();
  searchBySelect = new FormControl();
  searchByBoolean = new FormControl();
  public customView: ViewContainerRef;
  configChangeHandler = () => {
    const var0 = this.searchLevel1.value as ISearchConfig;
    if (var0 && var0.type === 'custom' && this.customView) {
      const factory = this.componentFactoryResolver.resolveComponentFactory(var0.component);
      this.customView.remove()
      const ref = this.customView.createComponent(factory);
      var0.componentInputMap && Object.keys(var0.componentInputMap).forEach(k => {
        ref.instance[k] = var0.componentInputMap[k]
      });
      ref.changeDetectorRef.detectChanges();
      if (ref.instance['ngOnChanges']) {
        (ref.instance as any).ngOnChanges();
      }
      (ref.instance as any).searchRef = this;
      var0.componentOutputMap && Object.keys(var0.componentOutputMap).forEach(k => {
        ref.instance[k].subscribe(next => { var0.componentOutputMap[k](next, this) })
      });
    }
  }
  @ViewChild('custom', { static: false, read: ViewContainerRef }) set spinner(view: ViewContainerRef) {
    if (!view) {
      this.customView = undefined;
    }
    if (this.customView === undefined && view) {
      this.customView = view;
      //will only be called once
      this.configChangeHandler()
    }
  }
  private subs: Subscription = new Subscription();
  separatorKeysCodes: number[] = [ENTER, COMMA];
  constructor(
    fb: FormBuilder,
    public translateSvc: TranslateService,
    private deviceSvc: DeviceService,
    private componentFactoryResolver: ComponentFactoryResolver,
  ) {
    this.searchLevel1.valueChanges.subscribe(() => {
      this.configChangeHandler()
    })
    this.options = fb.group({
      searchType: this.searchLevel1,
      searchQuery: this.searchQuery,
      searchByString: this.searchByString,
      searchBySelect: this.searchBySelect,
      searchByBoolean: this.searchByBoolean,
      searchByNumMin: this.searchByNumMin,
      searchByNumMax: this.searchByNumMax,
    });
    let sub3 = this.searchByNumMin.valueChanges.subscribe(next => {
      const var0 = [">=" + next];
      if (this.searchByNumMax.value) {
        var0.push("<=" + this.searchByNumMax.value)
      }
      this.searchQuery.setValue(var0)
    });
    let sub4 = this.searchByNumMax.valueChanges.subscribe(next => {
      const var0 = ["<=" + next];
      if (this.searchByNumMin.value) {
        var0.push(">=" + this.searchByNumMin.value)
      }
      this.searchQuery.setValue(var0)
    });
    let sub2 = this.searchQuery.valueChanges.pipe(filter(e => e !== null && e !== undefined && e !== '' && JSON.stringify(e) !== JSON.stringify([]))).pipe(debounce(() => interval(1000)))
      .subscribe(next => {
        this.search.emit({ value: this.getFinalQuery(this.searchLevel1.value, next), resetPage: true, key: (this.searchLevel1.value as ISearchConfig).key });
      });
    this.searchLevel1.valueChanges.subscribe(next => {
      const var0 = next as ISearchConfig;
      this.searchItems = [];
      this.options.reset({
        searchType: this.searchLevel1.value,
        searchQuery: '',
        searchByStringCtrl: '',
        searchByName: '',
        searchByNumMin: '',
        searchByNumMax: '',
      }, { emitEvent: false });
    });
    this.subs.add(sub2)
    this.subs.add(sub3)
    this.subs.add(sub4)
  }
  ngOnChanges(changes: SimpleChanges): void {
    if (this.searchConfigs.length > 0) {
      this.updateSearchValueBasedOnUrl()
    }
  }
  getFinalQuery(key: ISearchConfig, next: string[]): string {
    if (next && next.length > 0) {
      if (key.type === 'range') {
        return key.searchValue + ":" + (<Array<string>>next).join('$');
      }
      if (key.type === 'custom') {
        return key.searchValue + ":" + (<Array<string>>next).map(e => e.replace(":", "-")).join('.');
      }
      return key.searchValue + ":" + (key.multiple ? (<Array<string>>next).join(key.multiple.delimiter) : next[0]);
    } else {
      return '';
    }
  }
  private updateSearchValueBasedOnUrl() {
    const urlQuery = this.deviceSvc.getParams().query;
    const key = this.deviceSvc.getParams().key;
    if (urlQuery) {
      const splittedQuery = urlQuery.split(":");
      if (splittedQuery.length > 1) {
        const config = this.searchConfigs.find(e => {
          if (e.key) {
            return e.key === key
          } else {
            return e.searchValue === splittedQuery[0]
          }
        })

        if (config) {
          if (config.type === 'dropdown') {
            if (config.multiple) {
              this.searchItems = [...splittedQuery[1].split(config.multiple.delimiter).map(e => (config.source.find(ee => ee.value === e)))].filter(e => e);
            } else {
              const var0 = config.source.find(e => e.value === splittedQuery[1])
              if (var0) {
                this.searchItems = [var0];
              } else {
                this.searchItems = [];
              }
            }
            this.searchLevel1.setValue(config, { emitEvent: false });
            this.searchQuery.setValue(this.searchItems.map(e => e.value), { emitEvent: false })
            this.search.emit({ value: this.getFinalQuery(config, this.searchQuery.value), resetPage: false, key: config.key });
            return;
          }
          else if (config.type === 'text') {
            if (config.multiple) {
              this.searchItems = [...splittedQuery[1].split(config.multiple.delimiter).map(e => ({ label: e, value: e }))];
            } else {
              this.searchItems = [{ label: splittedQuery[1], value: splittedQuery[1] }];
            }
            this.searchLevel1.setValue(config, { emitEvent: false });
            this.searchQuery.setValue(this.searchItems.map(e => e.value), { emitEvent: false })
            this.search.emit({ value: this.getFinalQuery(config, this.searchQuery.value), resetPage: false, key: config.key });
            return;
          }
          else if (config.type === 'boolean') {
            this.searchItems = [{ label: splittedQuery[1], value: splittedQuery[1] }];
            this.searchLevel1.setValue(config, { emitEvent: false });
            this.searchByBoolean.setValue(splittedQuery[1], { emitEvent: false });
            this.searchQuery.setValue(this.searchItems.map(e => e.value), { emitEvent: false })
            this.search.emit({ value: this.getFinalQuery(config, this.searchQuery.value), resetPage: false, key: config.key });
            return;
          }
          else if (config.type === 'range') {
            this.searchItems = [...splittedQuery[1].split('$').map(e => ({ label: e, value: e }))];
            const greater = this.searchItems.find(e => e.label.includes(">="))
            if (greater) {
              this.searchByNumMin.setValue(greater.label.replace(">=", ""), { emitEvent: false })
            }
            const less = this.searchItems.find(e => e.label.includes("<="))
            if (less) {
              this.searchByNumMax.setValue(less.label.replace("<=", ""), { emitEvent: false })
            }
            this.searchLevel1.setValue(config, { emitEvent: false });
            this.searchByBoolean.setValue(splittedQuery[1], { emitEvent: false });
            this.searchQuery.setValue(this.searchItems.map(e => e.value), { emitEvent: false })
            this.search.emit({ value: this.getFinalQuery(config, this.searchQuery.value), resetPage: false, key: config.key });
            return;
          }
          else if (config.type === 'custom') {
            config.resumeFromUrl(this, splittedQuery[1]);
            return;
          } else {
            console.error('unknown type', config.type)
          }
        }
      }
    }
    this.search.emit({ value: '', resetPage: false });
  }
  ngOnInit(): void {
    const sub = this.deviceSvc.refreshSummary.subscribe(() => {
      this.search.emit({ value: this.getFinalQuery(this.searchLevel1.value, this.searchQuery.value), resetPage: false });
    })
    this.subs.add(sub)
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
  overwriteCommon(value: string) {
    this.searchQuery.setValue([value])
  }
  doReset() {
    this.searchItems = [];
    this.options.reset({
      searchType: null,
      searchQuery: '',
      searchByStringCtrl: '',
      searchByName: '',
      searchByAttr: '',
      searchByAttrSelect: '',
      searchBySelect: '',
      searchByBoolean: '',
      searchByAttrManual: '',
    }, { emitEvent: false });
    this.search.emit({ value: '', resetPage: true });
  }
  doRefresh() {
    this.search.emit({ value: this.getFinalQuery(this.searchLevel1.value, this.searchQuery.value), resetPage: false });
  }
  add(value: string) {
    this.searchItems.push({ label: value, value: value });
    this.searchQuery.setValue(this.searchItems.map(e => e.value))
  }
  remove(item: IOption): void {
    this.searchItems = this.searchItems.filter(e => e.value !== item.value)
    this.searchQuery.setValue(this.parseLable(this.searchItems))
  }

  onAutoCompleteClickHandler(selected: IOption): void {
    this.searchItems.push(selected);
    this.searchQuery.setValue(this.parseLable(this.searchItems))
  }
  parseLable(searchItems: IOption[]): string[] {
    return searchItems.map(e => e.value as string)
  }
  shouldDisableAddBtn() {
    const disable = (this.searchLevel1.value?.multiple) ? false : (this.searchItems.length > 0 ? true : false);
    return disable;
  }
  getAutoCompleteList() {
    return this.searchLevel1.value?.multiple ? this.searchLevel1.value?.source : (this.searchItems.length > 0 ? [] : this.searchLevel1.value?.source);
  }
}
