import { Component } from '@angular/core';
import { TableHelper } from 'src/app/clazz/table-helper';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { IProjectSimple } from 'src/app/misc/interface';
import { Utility } from 'src/app/misc/utility';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
@Component({
  selector: 'app-summary-project',
  templateUrl: './summary-project.component.html',
  styleUrls: []
})
export class SummaryProjectComponent {
  columnList = {
    id: 'ID',
    name: 'NAME',
    createdAt: 'PROJECT_CREATED_AT',
    createdBy: 'CREATED_BY',
  }
  searchConfigs: ISearchConfig[] = [
    {
      searchLabel: 'ID',
      searchValue: 'id',
      type: 'text',
      multiple: {
        delimiter: '.'
      }
    },
  ]
  private url = Utility.getMgmtResource(RESOURCE_NAME.MGMT_PROJECTS)
  public tableSource: TableHelper<IProjectSimple> = new TableHelper(this.columnList, 10, this.httpSvc, this.url);
  constructor(
    public router: RouterWrapperService,
    private httpSvc: HttpProxyService,
    private deviceSvc: DeviceService
  ) {
    this.deviceSvc.updateDocTitle('MGMT_PROJECT_DOC_TITLE')
    this.tableSource.loadPage(0)
  }
}
