import { Router } from '@angular/router';

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
        const params = router.routerState.snapshot.root.queryParams;
        const queryBinded: string[] = [];
        Object.keys(params).forEach(k => {
            queryBinded.push(k + "=" + params[k]);
        });
        window.location.assign('/login?' + queryBinded.join("&"))
    } else {
        window.location.assign('/login')
    }
}
export function uniqueString(input: string[]) {
    return new Array(...new Set(input));
}
export function uniqueObject<T>(input: T[], field: string) {
    return input.filter((e, i) => input.findIndex(ee => ee[field] === e[field]) === i)
}
export function createImageFromBlob(image: Blob, callback: (reader: FileReader) => void) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
        callback(reader)
    }, false);
    if (image) {
        reader.readAsDataURL(image);
    }
}