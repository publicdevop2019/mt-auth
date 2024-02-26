import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Observable } from 'rxjs';
import { ISumRep } from 'src/app/clazz/summary.component';
export interface IFlatNode {
  expandable: boolean;
  name: string;
  level: number;
}
export interface INode {
  id: string;
  name: string;
  editable?: boolean;
  enableI18n?: boolean;
  noChildren?: boolean;
  parentId?: string,
  nodes?: INode[],
}
@Component({
  selector: 'app-dynamic-tree',
  templateUrl: './dynamic-tree.component.html',
  styleUrls: ['./dynamic-tree.component.css']
})
export class DynamicTreeComponent implements OnInit {
  rootNodes: INode[] = []
  public flatNodes: INode[] = []
  @Input() loadRoot: Observable<ISumRep<INode>>
  @Input() loadChildren: (id: string) => Observable<ISumRep<INode>>;
  @Input() fg: FormGroup;
  //field to disable check/uncheck cascading, this is useful when we don't want parent to be auto checked/unchecked
  @Input() cascade: boolean = true;
  constructor() {

  }
  ngOnInit(): void {
    this.loadRoot.subscribe(next => {
      this.rootNodes = next.data;
      next.data.forEach(e => {
        this.flatNodes.push(e)
      })
    })
  }

}
