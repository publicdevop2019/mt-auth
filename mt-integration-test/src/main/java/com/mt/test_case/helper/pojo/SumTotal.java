package com.mt.test_case.helper.pojo;

import java.util.List;
import lombok.Data;
@Data
public class SumTotal<T> {
    private List<T> data;
    private Integer totalItemCount;
}
