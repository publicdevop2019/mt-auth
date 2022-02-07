export interface IEndpoint {
  resourceId: string;
  description?: string;
  path: string;
  method?: string;
  id: string;
  websocket: boolean;
  secured: boolean;
  csrfEnabled: boolean;
  corsProfileId?: string;
  cacheProfileId?: string;
  version: number;
}
export const HTTP_METHODS = [
  { label: 'HTTP_GET', value: "GET" },
  { label: 'HTTP_POST', value: "POST" },
  { label: 'HTTP_PUT', value: "PUT" },
  { label: 'HTTP_DELETE', value: "DELETE" },
  { label: 'HTTP_PATCH', value: "PATCH" },
]