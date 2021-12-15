export interface IFilterItem {
    id: string,
    name: string,
    values: string[]
  }
  export interface IBizFilter {
    id: string,
    catalogs: string[],
    description?:string
    filters: IFilterItem[]
    version:number;
    reviewRequired?:boolean
  }