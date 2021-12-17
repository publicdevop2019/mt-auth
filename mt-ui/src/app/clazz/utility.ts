import { Router } from '@angular/router';
import { IBizAttribute } from './validation/aggregate/attribute/interfaze-attribute';
import { ICatalog } from './validation/aggregate/catalog/interfaze-catalog';
import { hasValue } from './validation/validator-common';

export function getCookie(name: string): string {
    let value = "; " + document.cookie;
    let parts = value.split("; " + name + "=");
    if (parts.length == 2) return parts.pop().split(";").shift();
}
export function logout(router?: Router) {
    sessionStorage.clear();
    localStorage.removeItem('jwt');
    document.cookie = "jwt=;expires=Thu, 01 Jan 1970 00:00:00 UTC;path=/"
    if (router) {
        const params=router.routerState.snapshot.root.queryParams;
        const queryBinded: string[] = [];
        Object.keys(params).forEach(k => {
            queryBinded.push(k + "=" + params[k]);
        });
        console.dir(queryBinded.join("&"))
        window.location.assign('/login?' + queryBinded.join("&"))
    } else {
        window.location.assign('/login')
    }
}
export function getLabel(e: IBizAttribute): string {
    let lableNew: string[] = [];
    lableNew.push(e.name)
    if (e.description) {
        lableNew.push(e.description)
    }
    if (e.selectValues) {
        lableNew.push(e.selectValues.join(','))
    }
    return lableNew.join(' - ')
}
export function getLayeredLabel(attr: ICatalog, es: ICatalog[]): string {
    let tags: string[] = [];
    tags.push(attr.name);
    while (hasValue(attr.parentId)) {
        let nextId = attr.parentId;
        attr = es.find(e => e.id === nextId);
        tags.push(attr.name);
    }
    return tags.reverse().join(' / ')
}
export function parseAttributePayload(input: string[], attrList: IBizAttribute[]) {
    let parsed = {};
    input.forEach((attr, index) => {
        let selected = attrList.find(e => String(e.id) === attr.split(':')[0]);
        if (index === 0) {
            parsed['attributeId'] = selected.id;
            if (selected.method === 'SELECT') {
                parsed['attributeValueSelect'] = attr.split(':')[1];
            } else {
                parsed['attributeValueManual'] = attr.split(':')[1];
            }
        } else {
            parsed['attributeId_' + (index - 1)] = selected.id;
            if (selected.method === 'SELECT') {
                parsed['attributeValueSelect_' + (index - 1)] = attr.split(':')[1];
            } else {
                parsed['attributeValueManual_' + (index - 1)] = attr.split(':')[1];
            }
        }
    })
    return parsed;
}