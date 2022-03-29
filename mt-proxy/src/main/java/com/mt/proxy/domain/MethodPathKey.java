package com.mt.proxy.domain;

import com.google.common.base.Objects;
import lombok.Getter;

@Getter
public class MethodPathKey {
    private final String method;
    private final String path;

    public MethodPathKey(String method, String path) {
        this.method = method;
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MethodPathKey that = (MethodPathKey) o;
        return Objects.equal(method, that.method)
            &&
            Objects.equal(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(method, path);
    }

    @Override
    public String toString() {
        return "MethodPathKey{"
            +
            "method='" + method + '\''
            +
            ", path='" + path + '\''
            +
            '}';
    }
}
