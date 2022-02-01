import { Component, OnInit, Input, OnChanges, SimpleChanges, Output, EventEmitter } from '@angular/core';
import { FlatTreeControl } from '@angular/cdk/tree';
import { MatTreeFlattener, MatTreeFlatDataSource } from '@angular/material/tree';
import { hasValue } from 'src/app/clazz/validation/validator-common';
export interface IFlatNode {
  expandable: boolean;
  name: string;
  level: number;
}
export interface INode {
  id: string;
  name: string;
  parentId?: string,
  nodes?: INode[],
}
@Component({
  selector: 'app-tree',
  templateUrl: './tree.component.html',
  styleUrls: ['./tree.component.css']
})
export class TreeComponent implements OnInit, OnChanges {
  @Input() catalogs: INode[];
  @Output() leafNodeClicked = new EventEmitter<INode>();
  @Output() nonLeafNodeClicked = new EventEmitter<INode>();
  treeControl = new FlatTreeControl<IFlatNode>(node => node.level, node => node.expandable);
  private _transformer = (node: INode, level: number) => {
    return {
      expandable: !!node.nodes && node.nodes.length > 0,
      name: node.name,
      level: level,
      id: node.id
    };
  }
  treeFlattener = new MatTreeFlattener(
    this._transformer, node => node.level, node => node.expandable, node => node.nodes);

  treeDataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);
  constructor() { }
  ngOnChanges(changes: SimpleChanges): void {
    if (this.catalogs){
      this.treeDataSource.data = TreeComponent.convertToTree(this.catalogs);
    }
  }

  ngOnInit() {
  }
  hasChild = (_: number, node: IFlatNode) => node.expandable;
  public static  notLeafNode(catalogs: INode[], nodes: INode[]): boolean {
    return nodes.filter(node => {
      return catalogs.filter(el => el.parentId === node.id).length >= 1
    }).length >= 1
  }
  public static convertToTree(nodes: INode[]): INode[] {
    let rootNodes = nodes.filter(e => !hasValue(e.parentId));
    let treeNodes = rootNodes.map(e => <INode>{
      id: e.id,
      name: e.name,
    });
    let currentLevel = treeNodes;
    while (this.notLeafNode(nodes, currentLevel)) {
      let nextLevelCol: INode[] = []
      currentLevel.forEach(childNode => {
        let nextLevel = nodes.filter(el => el.parentId === childNode.id).map(e => <INode>{
          id: e.id,
          name: e.name,
        });
        childNode.nodes = nextLevel;
        nextLevelCol.push(...nextLevel);
      });
      currentLevel = nextLevelCol;
    }
    return treeNodes;
  }
  emitBranchNodeClick(id: string) {
    this.nonLeafNodeClicked.emit(this.catalogs.find(e => e.id === id))
  }
  emitLeafNodeClick(id: string) {
    this.leafNodeClicked.emit(this.catalogs.find(e => e.id === id))
  }
}
