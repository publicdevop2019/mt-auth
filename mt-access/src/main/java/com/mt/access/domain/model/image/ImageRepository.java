package com.mt.access.domain.model.image;

import java.util.Optional;

public interface ImageRepository {
    Optional<Image> query(ImageId id);

    void add(Image image);
}
