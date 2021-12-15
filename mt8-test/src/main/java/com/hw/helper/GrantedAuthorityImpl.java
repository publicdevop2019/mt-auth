package com.hw.helper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

/**
 * rename filed to avoid setter & getter type different
 *
 * @param <T>
 */
public class GrantedAuthorityImpl<T extends Enum<T>> implements GrantedAuthority {
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GrantedAuthorityImpl<?> that = (GrantedAuthorityImpl<?>) o;
        return grantedAuthority.equals(that.grantedAuthority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(grantedAuthority);
    }

    @Setter
    @Getter
    private T grantedAuthority;

    public GrantedAuthorityImpl(T input) {
        grantedAuthority = input;
    }

    public GrantedAuthorityImpl() {
    }

    public static <T extends Enum> GrantedAuthorityImpl getGrantedAuthority(Class<T> enumType, String string) {
        return new GrantedAuthorityImpl(T.valueOf(enumType, string));
    }

    @JsonIgnore
    public String getAuthority() {
        return grantedAuthority.toString();
    }

}