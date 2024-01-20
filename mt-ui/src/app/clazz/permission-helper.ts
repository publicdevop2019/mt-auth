import { combineLatest, Observable, ReplaySubject } from "rxjs"
import { map } from "rxjs/operators"
import { IProjectUiPermission } from "../services/project.service"

export class PermissionHelper {
    permissionObserble: ReplaySubject<IProjectUiPermission>

    constructor(permissionObserble: ReplaySubject<IProjectUiPermission>) {
        this.permissionObserble = permissionObserble;
    }

    canDo(projectId: string, userPermissionIds: string[], ...name: string[]) {
        return combineLatest([this.permissionObserble]).pipe(map(e => {
            return this.hasPermission(e[0], projectId, name, userPermissionIds)
        }))
    }

    extractResult(result: Observable<{ result: boolean, projectId: string }>) {
        return result.pipe(map(e => e.result))
    }

    private hasPermission(permissions: IProjectUiPermission, projectId: string, name: string[], permissionIds: string[]) {
        const pId = permissions.permissionInfo.filter(e => name.includes(e.name)).map(e => e.id)
        if (pId.length > 0) {
            return {
                result: !(pId.filter(e => !permissionIds.includes(e)).length > 0),
                projectId: projectId
            }
        } else {
            return {
                result: false,
                projectId: projectId
            }
        }
    }
}