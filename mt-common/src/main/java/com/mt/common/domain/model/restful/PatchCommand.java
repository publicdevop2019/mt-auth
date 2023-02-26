package com.mt.common.domain.model.restful;

import com.mt.common.CommonConstant;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.ExceptionCatalog;
import com.mt.common.domain.model.exception.HttpResponseCode;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * used for json patch and create/delete operation.
 */
@Data
public class PatchCommand implements Comparable<PatchCommand>, Serializable {
    private static final long serialVersionUID = 1;
    private String op;
    private String path;
    private Object value;
    private Integer expect;

    public static List<PatchCommand> buildRollbackCommand(List<PatchCommand> patchCommands) {
        List<PatchCommand> deepCopy =
            CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(patchCommands);
        deepCopy.forEach(e -> {
            if (e.getOp().equalsIgnoreCase(CommonConstant.PATCH_OP_TYPE_SUM)) {
                e.setOp(CommonConstant.PATCH_OP_TYPE_DIFF);
            } else if (e.getOp().equalsIgnoreCase(CommonConstant.PATCH_OP_TYPE_DIFF)) {
                e.setOp(CommonConstant.PATCH_OP_TYPE_SUM);
            } else {
                throw new DefinedRuntimeException("rollback not supported", "0020",
                    HttpResponseCode.BAD_REQUEST,
                    ExceptionCatalog.ILLEGAL_STATE);
            }
        });
        return deepCopy;
    }

    @Override
    public int compareTo(PatchCommand to) {
        if (parseDomainId(path).equals(parseDomainId(to.path))) {
            return 0;
        } else {
            return 1;
        }
    }

    private Long parseId(String path) {
        String[] split = path.split("/");
        return Long.parseLong(split[1]);
    }

    private String parseDomainId(String path) {
        String[] split = path.split("/");
        return split[1];
    }
}
