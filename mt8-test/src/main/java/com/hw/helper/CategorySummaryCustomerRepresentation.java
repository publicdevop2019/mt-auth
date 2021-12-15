package com.hw.helper;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CategorySummaryCustomerRepresentation {
    private List<CategorySummaryCardRepresentation> data;
}
