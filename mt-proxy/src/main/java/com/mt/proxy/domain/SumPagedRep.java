package com.mt.proxy.domain;

import java.util.List;
import lombok.Data;

@Data
public class SumPagedRep<T> {
    protected List<T> data;
    protected Long totalItemCount;
}
