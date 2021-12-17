package com.hw.helper;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class UpdateTagCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String name;
    private String description;
    private TagValueType method;
    @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> selectValues;
    private TagType type;
    private Integer version;
}
