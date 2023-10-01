package com.mt.access.domain.model.image;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import java.io.IOException;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@Slf4j
@EqualsAndHashCode
public class Image {
    @Setter
    @Getter
    protected Long id;
    @Setter
    @Getter
    private String createdBy;
    @Getter
    @Setter
    private Long createdAt;
    private ImageId imageId;
    @Setter
    @Getter
    private byte[] source;
    private String originalName;
    private String contentType;

    public Image(ImageId id, MultipartFile file, Integer allowedSize, List<String> allowedTypes) {
        validateUploadCriteria(file, allowedSize, allowedTypes);
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.contentType = file.getContentType();
        this.originalName = file.getOriginalFilename();
        this.imageId = id;
        try {
            this.source = file.getBytes();
        } catch (IOException e) {
            throw new DefinedRuntimeException("error during saving image file", "1045",
                HttpResponseCode.BAD_REQUEST, e);
        }
    }

    public static Image fromDatabaseRow(Long id, Long createdAt, String createdBy,
                                        String contentType, ImageId domainId, String originalName,
                                        byte[] sources) {
        Image image = new Image();
        image.setId(id);
        image.setCreatedAt(createdAt);
        image.setCreatedBy(createdBy);
        image.setContentType(contentType);
        image.setImageId(domainId);
        image.setOriginalName(originalName);
        image.setSource(sources);
        return image;
    }

    /**
     * validate file type, file size.
     */
    private void validateUploadCriteria(MultipartFile file, Integer allowedSize,
                                        List<String> allowedTypes) {
        if (allowedTypes.stream().noneMatch(e -> e.equals(file.getContentType()))) {
            throw new DefinedRuntimeException("file type not allowed, got " + file.getContentType(),
                "1046",
                HttpResponseCode.BAD_REQUEST);
        }
        try {
            if (file.getBytes().length > allowedSize) {
                throw new DefinedRuntimeException("file size not allowed", "1047",
                    HttpResponseCode.BAD_REQUEST);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
