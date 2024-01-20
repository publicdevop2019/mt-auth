import { environment } from "src/environments/environment";
import { EntityCommonService } from "./entity.common-service";
import { IIdBasedEntity } from "./summary.component";
import { getUrl } from "../misc/utility";
import { APP_CONSTANT } from "../misc/constant";

export class TenantEntityService<S extends IIdBasedEntity, D> extends EntityCommonService<S, D>{
    protected entityName: string = '';
    protected projectId: string = '';
    entityRepo: string = getUrl([environment.serverUri, APP_CONSTANT.MT_AUTH_ACCESS_PATH, 'projects', this.projectId, this.entityName]);
    setProjectId(id: string) {
        this.projectId = id;
        this.entityRepo = getUrl([environment.serverUri, APP_CONSTANT.MT_AUTH_ACCESS_PATH, 'projects', this.projectId, this.entityName]);
    }
    getProjectId(){
        return this.projectId;
    }
}