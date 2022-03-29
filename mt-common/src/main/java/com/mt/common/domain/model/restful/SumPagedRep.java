package com.mt.common.domain.model.restful;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class SumPagedRep<T> implements Serializable {
    protected List<T> data = new ArrayList<>();
    protected Long totalItemCount;

    public SumPagedRep(List<T> data, Long count) {
        this.data = data;
        this.totalItemCount = count;
    }

    public <S> SumPagedRep(SumPagedRep<S> original, Function<S, T> data) {
        this.data = original.getData().stream().map(data).collect(Collectors.toList());
        this.totalItemCount = original.getTotalItemCount();
    }

    public SumPagedRep() {
    }

    public static <T> SumPagedRep<T> empty() {
        SumPagedRep<T> rep = new SumPagedRep<>();
        rep.totalItemCount = 0L;
        return rep;
    }

    public Optional<T> findFirst() {
        if (data.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(data.get(0));
    }
}
