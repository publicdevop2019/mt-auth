import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ISumRep } from 'mt-form-builder/lib/classes/template.interface';
import { Observable } from 'rxjs';
import { INode } from '../tree/tree.component';

@Component({
  selector: 'app-dynamic-tree',
  templateUrl: './dynamic-tree.component.html',
  styleUrls: ['./dynamic-tree.component.css']
})
export class DynamicTreeComponent implements OnInit {
  rootNodes: INode[] = []
  public flatNodes: INode[] = []
  @Input() edit: boolean
  @Input() loadRoot: Observable<ISumRep<INode>>
  @Input() loadChildren: (id: string) => Observable<ISumRep<INode>>;
  @Input() fg: FormGroup;
  constructor() {

  }
  ngOnInit(): void {
    this.loadRoot.subscribe(next => {
      this.rootNodes = next.data;
      next.data.forEach(e=>{
        this.flatNodes.push(e)  
      })
    })
  }

}
