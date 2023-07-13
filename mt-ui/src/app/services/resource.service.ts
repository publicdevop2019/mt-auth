import { HttpClient, HttpHeaders } from "@angular/common/http";
import { environment } from "src/environments/environment";
import { ISumRep } from "../clazz/summary.component";
import { Utility } from "../misc/utility";
import { Injectable } from "@angular/core";
@Injectable({
    providedIn: 'root'
})
export class ResourceService {
    constructor(public httpClient: HttpClient) {

    }

    getByQuery<T>(resourceUrl: string, num: number, size: number, by?: string, order?: string, headers?: {}) {
        let headerConfig = new HttpHeaders();
        headers && Object.keys(headers).forEach(e => {
            headerConfig = headerConfig.set(e, headers[e] + '')
        })
        return this.httpClient.get<ISumRep<T>>(this.getResourceUrl(resourceUrl, this.getPageParam(num, size, by, order)), { headers: headerConfig })
    }

    private getResourceUrl(resourceUrl: string, pageConfig: string) {
        return environment.serverUri + resourceUrl + (resourceUrl.includes('?') ? '&' + pageConfig : '?' + pageConfig)
    }
    private getPageParam(pageNumer?: number, pageSize?: number, sortBy?: string, sortOrder?: string): string {
        let var1: string[] = [];
        if (Utility.hasValue(pageNumer) && Utility.hasValue(pageSize)) {
            if (sortBy && sortOrder) {
                var1.push('num:' + pageNumer)
                var1.push('size:' + pageSize)
                var1.push('by:' + sortBy)
                var1.push('order:' + sortOrder)
                return "page=" + var1.join(',')
            } else {
                var1.push('num:' + pageNumer)
                var1.push('size:' + pageSize)
                return "page=" + var1.join(',')
            }
        }
        return ''
    }
}