package com.mt.access.domain.model.image;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "image")
@Data
@NoArgsConstructor
@Slf4j
public class Image extends Auditable {
    @Embedded
    private ImageId imageId;
    @Setter
    @Getter
    @Lob
    private byte[] source;
    @Column
    private String originalName;

    @Column
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
            log.error("error during saving file", e);
            throw new FileUploadException();
        }
    }

    /**
     * validate file type, file size.
     */
    private void validateUploadCriteria(MultipartFile file, Integer allowedSize,
                                        List<String> allowedTypes) {
        if (allowedTypes.stream().noneMatch(e -> e.equals(file.getContentType()))) {
            throw new IllegalArgumentException("file type not allowed");
        }
        try {
            if (file.getBytes().length > allowedSize) {
                throw new IllegalArgumentException("file size not allowed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Image image = (Image) o;
        return Objects.equals(imageId, image.imageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageId);
    }
}
