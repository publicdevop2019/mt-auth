package com.hw.helper;

import lombok.Data;

import java.io.Serializable;

/**
 * used for json patch and create/delete operation
 */
@Data
public class PatchCommand implements Comparable<PatchCommand>, Serializable {
    private static final long serialVersionUID = 1;
    private String op;
    private String path;
    private Object value;
    private Integer expect;

    @Override
    public int compareTo(PatchCommand to) {
        if (parseId(path).equals(parseId(to.path)))
            return 0;
        else if (parseId(path) > parseId(to.path))
            return 1;
        else
            return -1;
    }

    private Long parseId(String path) {
        String[] split = path.split("/");
        return Long.parseLong(split[1]);
    }
}
