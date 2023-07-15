package com.mt.access.application.image;


import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.image.Image;
import com.mt.access.domain.model.image.ImageId;
import com.mt.access.domain.model.image.ImageQuery;
import com.mt.common.application.CommonApplicationServiceRegistry;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageApplicationService {
    private static final List<String> ALLOWED_TYPE = List.of("image/jpeg","image/png");
    private static final Integer ALLOWED_SIZE = 1024000;
    private static final String IMAGE = "IMAGE";


    public ImageId create(String changeId, MultipartFile file) {
        ImageId imageId = new ImageId();
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (change) -> {
            Image image = new Image(imageId, file, ALLOWED_SIZE, ALLOWED_TYPE);
            DomainRegistry.getImageRepository().add(image);
            return null;
        }, IMAGE);
        return imageId;
    }

    public Optional<Image> queryById(String id) {
        return DomainRegistry.getImageRepository().query(new ImageQuery(new ImageId(id)))
            .findFirst();
    }
}
