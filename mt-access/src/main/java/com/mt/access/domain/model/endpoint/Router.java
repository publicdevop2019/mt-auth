package com.mt.access.domain.model.endpoint;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.endpoint.event.RouterKeyParamChanged;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;
import java.time.Instant;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@NoArgsConstructor
@Getter
public class Router extends Auditable {
    private static final Pattern PATH_REGEX = Pattern.compile("^[a-z\\-/]*$");

    @Setter(AccessLevel.PRIVATE)
    private RouterId routerId;
    @Setter(AccessLevel.PRIVATE)
    private ProjectId projectId;
    private String name;
    private String description;
    private String path;
    private ExternalUrl externalUrl;

    public static Router addNewRouter(ProjectId projectId, String name, String description,
                                      String path, RouterId routerId, ExternalUrl externalUrl,
                                      TransactionContext context) {
        Router router = new Router();
        router.setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        router.setVersion(0);
        router.setRouterId(routerId);
        router.setProjectId(projectId);
        router.setName(name);
        router.setDescription(description);
        router.setPath(path);
        router.setExternalUrl(externalUrl);
        long milli = Instant.now().toEpochMilli();
        router.setCreatedAt(milli);
        router.setCreatedBy(DomainRegistry.getCurrentUserService().getUserId().getDomainId());
        router.setModifiedAt(milli);
        router.setModifiedBy(DomainRegistry.getCurrentUserService().getUserId().getDomainId());
        context
            .append(new RouterKeyParamChanged(routerId));
        return router;
    }

    private void setExternalUrl(ExternalUrl externalUrl) {
        this.externalUrl = externalUrl;
    }

    private void setName(String name) {
        Validator.validRequiredString(1, 50, name);
        this.name = name;
    }

    private void setPath(String path) {
        Validator.notBlank(path);
        Validator.lessThanOrEqualTo(path, 50);
        Validator.greaterThanOrEqualTo(path, 5);
        Matcher matcher = PATH_REGEX.matcher(path);//alpha - / only
        boolean result = false;
        if (matcher.find()) {
            if (!path.startsWith("/") && !path.endsWith("/")) { //avoid /test/
                if (!path.endsWith("-") && !path.startsWith("-")) { //avoid -test-
                    if (path.contains("/")) {
                        boolean valid = true;
                        for (String s : path.split("/")) {
                            if (s.startsWith("-") || s.endsWith("-")) {
                                valid = false;
                                break;
                            }
                            if (s.isBlank()) {
                                valid = false;
                                break;
                            }
                        }
                        if (valid) {
                            result = true;
                        }
                    } else {
                        result = true;
                    }
                }
            }
        }
        if (!result) {
            throw new DefinedRuntimeException("invalid path format", "1084",
                HttpResponseCode.BAD_REQUEST);
        }
        this.path = path;
    }

    private void setDescription(String description) {
        Validator.validOptionalString(100, description);
        this.description = description;
    }

    public static Router fromDatabaseRow(Long id, Long createdAt, String createdBy, Long modifiedAt,
                                         String modifiedBy, Integer version,
                                         RouterId domainId, String name,
                                         String description, String path, ProjectId projectId,
                                         ExternalUrl externalUrl) {
        Router router = new Router();
        router.setId(id);
        router.setCreatedAt(createdAt);
        router.setCreatedBy(createdBy);
        router.setModifiedAt(modifiedAt);
        router.setModifiedBy(modifiedBy);
        router.setVersion(version);
        router.setRouterId(domainId);
        router.setProjectId(projectId);
        router.setName(name);
        router.setDescription(description);
        router.setPath(path);
        router.setExternalUrl(externalUrl);
        return router;
    }

    public boolean sameAs(Router o) {
        return Objects.equals(description, o.description) &&
            Objects.equals(name, o.name) &&
            Objects.equals(path, o.path) &&
            Objects.equals(routerId, o.routerId) &&
            Objects.equals(externalUrl, o.externalUrl);
    }

    public void removeIfNoEndpoints() {
        SumPagedRep<Endpoint> query =
            DomainRegistry.getEndpointRepository().query(new EndpointQuery(this.routerId));
        if (query.findFirst().isEmpty()) {
            DomainRegistry.getRouterRepository().remove(this);
        } else {
            throw new DefinedRuntimeException("router has endpoint linked", "1101",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public Router update(String name, String description, String path, ExternalUrl externalUrl,
                         TransactionContext context) {
        Router updated = CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        updated.setPath(path);
        updated.setExternalUrl(externalUrl);
        updated.setName(name);
        updated.setDescription(description);
        if (!path.equals(updated.path) || !externalUrl.equals(updated.externalUrl)) {
            context
                .append(new RouterKeyParamChanged(routerId));
        }
        return updated;
    }
}
