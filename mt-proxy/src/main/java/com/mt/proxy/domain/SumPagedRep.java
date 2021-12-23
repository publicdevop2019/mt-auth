package com.mt.proxy.domain;

import lombok.Data;

import java.util.List;

@Data
public class SumPagedRep<T> {
    protected List<T> data;
    protected Long totalItemCount;
}
