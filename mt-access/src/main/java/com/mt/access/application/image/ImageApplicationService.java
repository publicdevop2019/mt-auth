package com.mt.access.application.image;


import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.image.Image;
import com.mt.access.domain.model.image.ImageId;
import com.mt.access.domain.model.image.ImageQuery;
import com.mt.common.application.CommonApplicationServiceRegistry;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageApplicationService {
    public static final String IMAGE = "IMAGE";
    @Value("${allowed.types}")
    private List<String> allowedTypes;

    @Value("${allowed.size}")
    private Integer allowedSize;


    public ImageId create(String changeId, MultipartFile file) {
        ImageId imageId = new ImageId();
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (change) -> {
            Image image = new Image(imageId, file, allowedSize, allowedTypes);
            DomainRegistry.getImageRepository().add(image);
            return null;
        }, IMAGE);
        return imageId;
    }

    public Optional<Image> queryById(String id) {
        return DomainRegistry.getImageRepository().imageOfQuery(new ImageQuery(new ImageId(id)))
            .findFirst();
    }
}
