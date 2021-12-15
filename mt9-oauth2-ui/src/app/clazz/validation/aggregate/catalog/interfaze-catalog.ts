
export interface ICatalog {
  id: string;
  name: string;
  parentId?: string,
  attributes?: string[],
  catalogType?: 'BACKEND' | 'FRONTEND',
  version: number
}