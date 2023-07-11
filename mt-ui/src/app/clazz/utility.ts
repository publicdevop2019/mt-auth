import { Router } from '@angular/router';
import { HttpProxyService } from '../services/http-proxy.service';
import * as UUID from 'uuid/v1';
import { environment } from 'src/environments/environment';
export function getCookie(name: string): string {
    let value = "; " + document.cookie;
    let parts = value.split("; " + name + "=");
    if (parts.length == 2) return parts.pop().split(";").shift();
}
export function logout(router?: Router, httpProxy?: HttpProxyService) {
    if (httpProxy) {
        httpProxy.clearLogoutCheck()
    }
    if (httpProxy) {
        httpProxy.currentUserAuthInfo = undefined;
    }
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
export function copyOf<T>(input: T) {
    return JSON.parse(JSON.stringify(input)) as T
}
export function getUrl(input: string[]) {
    return input.join('/')
}
export class Utility {

    public static notEmpty(input?: any[]): boolean {
        return input && input.length > 0
    }
    public static copyOf<T>(source: T): T {
        return JSON.parse(JSON.stringify(source))
    }
    public static noEmptyString(input: string) {
        return input ? input : null
    }
    public static getChangeId() {
        return UUID();
    }
}
export class Logger {
    private static LOG_LEVEL: 'DEBUG' | 'TRACE' | 'ERROR' = environment.production ? 'ERROR' : 'DEBUG'
    public static debug(message: any, ...args: any[]) {
        if (Logger.LOG_LEVEL === 'DEBUG') {
            this.log(message, args)
        }
    }
    public static trace(message: any, ...args: any[]) {
        if (Logger.LOG_LEVEL === 'TRACE') {
            this.log(message, args)
        }
    }
    public static error(message: any, ...args: any[]) {
        if (Logger.LOG_LEVEL === 'ERROR') {
            this.log(message, args)
        }
    }
    public static log(message: string, args: any[]) {
        args.forEach(e => {
            if (typeof e !== 'string' || typeof e !== 'boolean') {
                message = message.replace('{}', JSON.stringify(e))
            } else {
                message = message.replace('{}', e)
            }
        })
        console.log(message)
    }
    public static debugObj(message: any, ...args: any[]) {
        if (Logger.LOG_LEVEL === 'DEBUG') {
            this.logObj(message, args)
        }
    }
    public static traceObj(message: any, ...args: any[]) {
        if (Logger.LOG_LEVEL === 'TRACE') {
            this.logObj(message, args)
        }
    }
    public static errorObj(message: any, ...args: any[]) {
        if (Logger.LOG_LEVEL === 'ERROR') {
            this.logObj(message, args)
        }
    }
    public static logObj(message: string, args: any[]) {
        console.log(message)
        args.forEach(arg => {
            console.dir(arg)
        })
    }
}