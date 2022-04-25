package com.mt.access.domain.model.image;

import com.mt.common.domain.model.restful.SumPagedRep;

public interface ImageRepository {
    SumPagedRep<Image> imageOfQuery(ImageQuery query);

    void add(Image image);
}
