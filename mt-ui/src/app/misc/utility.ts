import { HttpProxyService } from '../services/http-proxy.service';
import * as UUID from 'uuid/v1';
import { APP_CONSTANT } from './constant';
import { RouterWrapperService } from '../services/router-wrapper';
import { environment } from 'src/environments/environment';
import { IOption } from './interface';


export class Utility {
    static mergeUnique<T extends IOption>(source: T[], list: T[]): T[] {
        source.push(...list);
        return source.filter((e, i) => source.findIndex(ee => ee.value === e.value) === i)
    }
    static createImageFromBlob(image: Blob, callback: (reader: FileReader) => void) {
        let reader = new FileReader();
        reader.addEventListener("load", () => {
            callback(reader)
        }, false);
        if (image) {
            reader.readAsDataURL(image);
        }
    }
    static getCookie(name: string): string {
        let value = "; " + document.cookie;
        let parts = value.split("; " + name + "=");
        if (parts.length == 2) return parts.pop().split(";").shift();
    }

    static logout(router?: RouterWrapperService, httpProxy?: HttpProxyService) {
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
            const params = router.getParam();
            const queryBinded: string[] = [];
            Object.keys(params).forEach(k => {
                queryBinded.push(k + "=" + params[k]);
            });
            window.location.assign('/login?' + queryBinded.join("&"))
        } else {
            window.location.assign('/login')
        }
    }
    static getUrl(input: string[]) {
        return input.join('/')
    }
    static getTenantUrl(projectId: string, resourceUrl: string): string {
        return "/" + APP_CONSTANT.MT_AUTH_ACCESS_PATH + '/projects/' + projectId + resourceUrl
    }
    public static hasValue(input: any): boolean {
        return input !== null && input !== undefined && input !== '';
    }
    public static notEmpty(input?: any[]): boolean {
        return input && input.length > 0
    }
    public static copyOf<T>(source: T): T {
        return JSON.parse(JSON.stringify(source))
    }
    public static noEmptyString(input: string) {
        return input ? input : null
    }
    public static getChangeId(): string {
        return UUID();
    }
    public static getRandomString(): string {
        return Utility.getChangeId().replace(new RegExp(/[\d-]/g), '')
    }
    public static getProjectResource(projectId: string, resourceName: string) {
        return Utility.getUrl([environment.serverUri, APP_CONSTANT.MT_AUTH_ACCESS_PATH, 'projects', projectId, resourceName]);
    }
    public static getMgmtResource(resourceName: string) {
        return Utility.getUrl([environment.serverUri, APP_CONSTANT.MT_AUTH_ACCESS_PATH, 'mgmt', resourceName]);
    }
    public static getUserResource(resourceName: string) {
        return Utility.getUrl([environment.serverUri, APP_CONSTANT.MT_AUTH_ACCESS_PATH, 'user', resourceName]);
    }
    public static isLocalhost() {
        return location.hostname.includes('localhost')
    }

}
