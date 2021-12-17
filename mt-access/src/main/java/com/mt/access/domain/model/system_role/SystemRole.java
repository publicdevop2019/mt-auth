package com.mt.access.domain.model.system_role;

import com.mt.access.application.system_role.command.CreateSystemRoleCommand;
import com.mt.access.application.system_role.command.ReplaceSystemRoleCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.system_role.event.SystemRoleDeleted;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Table
@Entity
@NoArgsConstructor
@Getter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,region = "systemRoleRegion")
public class SystemRole extends Auditable {
    private static final List<String> nonDeletableIds;

    static {
        nonDeletableIds = List.of(
                AppConstant.ADMIN_USER_ID,
                AppConstant.BACKEND_ID,
                AppConstant.USER_USER_ID,
                AppConstant.ROOT_USER_ID,
                AppConstant.ROOT_CLIENT_ID,
                AppConstant.FIRST_PARTY_ID
        );
    }

    @Id
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private Long id;
    @Embedded
    private SystemRoleId roleId;
    @Convert(converter = RoleType.DBConverter.class)
    private RoleType roleType;
    private String name;
    private String description;

    public static SystemRole create(SystemRoleId systemRoleId, CreateSystemRoleCommand command) {
        SystemRole systemRole = new SystemRole();
        systemRole.setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        systemRole.setRoleId(systemRoleId);
        systemRole.setRoleType(command.getType());
        systemRole.setName(command.getName());
        systemRole.setDescription(command.getDescription());
        return systemRole;
    }

    public void setRoleId(SystemRoleId roleId) {
        this.roleId = roleId;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void replace(ReplaceSystemRoleCommand command) {
        this.name = command.getName();
        this.description = command.getDescription();
    }

    public void replace(String name, String description) {
        this.name = name;
        this.description = description;
    }

    private boolean removable() {
        return !nonDeletableIds.contains(this.roleId.getDomainId());
    }

    public void remove() {
        if(removable()){
            DomainRegistry.getSystemRoleRepository().remove(this);
            DomainEventPublisher.instance().publish(new SystemRoleDeleted(this.roleId));
        }else{
            throw new IllegalArgumentException("not allowed to delete");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SystemRole that = (SystemRole) o;
        return Objects.equals(id, that.id) && Objects.equals(roleId, that.roleId) && roleType == that.roleType && Objects.equals(name, that.name) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, roleId, roleType, name, description);
    }
}
