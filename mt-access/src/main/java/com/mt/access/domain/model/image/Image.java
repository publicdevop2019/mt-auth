package com.mt.access.domain.model.image;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import java.io.IOException;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
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
            throw new DefinedRuntimeException("error during saving image file", "1045",
                HttpResponseCode.BAD_REQUEST, e);
        }
    }

    /**
     * validate file type, file size.
     */
    private void validateUploadCriteria(MultipartFile file, Integer allowedSize,
                                        List<String> allowedTypes) {
        if (allowedTypes.stream().noneMatch(e -> e.equals(file.getContentType()))) {
            throw new DefinedRuntimeException("file type not allowed", "1046",
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
