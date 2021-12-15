package com.hw.helper;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class CategorySummaryCardRepresentation {
    private String name;
    private Set<String> attributes;
}