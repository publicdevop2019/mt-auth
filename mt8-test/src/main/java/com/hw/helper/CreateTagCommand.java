package com.hw.helper;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class CreateTagCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String name;
    private String description;
    private TagValueType method;
    private Set<String> selectValues;
    private TagType type;
}
