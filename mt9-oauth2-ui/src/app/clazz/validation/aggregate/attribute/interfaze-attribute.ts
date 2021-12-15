export interface IBizAttribute {
    id: string,
    name: string,
    description?: string,
    selectValues?: string[],
    method: 'MANUAL' | 'SELECT',
    type: 'PROD_ATTR' | 'SALES_ATTR' | 'KEY_ATTR' | 'GEN_ATTR',
    version:number
  }