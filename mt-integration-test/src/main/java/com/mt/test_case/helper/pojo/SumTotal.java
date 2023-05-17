package com.mt.test_case.helper.pojo;

import lombok.Data;

import java.util.List;
@Data
public class SumTotal<T> {
    private List<T> data;
    private Integer totalItemCount;
}
