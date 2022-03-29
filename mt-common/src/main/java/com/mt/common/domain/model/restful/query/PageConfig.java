package com.mt.common.domain.model.restful.query;

import static com.mt.common.CommonConstant.COMMON_ENTITY_ID;

import javax.annotation.Nullable;
import lombok.Getter;

@Getter
public class PageConfig {
    public static final String PAGING_NUM = "num";
    public static final String PAGING_SIZE = "size";
    public static final String SORT_BY = "by";
    public static final String SORT_ORDER = "order";
    private final String rawValue;
    protected String sortBy = COMMON_ENTITY_ID;
    protected boolean sortOrderAsc = true;
    private Long pageNumber = 0L;
    private Integer pageSize = 10;

    private PageConfig(@Nullable String configStr) {
        if (configStr == null) {
            rawValue = getDefaultConfig();
        } else {
            rawValue = configStr;
            String[] split = configStr.split(",");
            for (String str : split) {
                String[] split1 = str.split(":");
                if (split1.length != 2 || split1[0].isBlank() || split1[1].isBlank()) {
                    throw new PagingParseException();
                }
                if (PAGING_NUM.equalsIgnoreCase(split1[0])) {
                    try {
                        pageNumber = Long.parseLong(split1[1]);
                    } catch (Exception ex) {
                        throw new PagingParseException();
                    }
                }
                if (PAGING_SIZE.equalsIgnoreCase(split1[0])) {
                    try {
                        pageSize = Integer.parseInt(split1[1]);
                    } catch (Exception ex) {
                        throw new PagingParseException();
                    }
                }
                if (SORT_BY.equalsIgnoreCase(split1[0])) {
                    sortBy = split1[1];
                }
                if (SORT_ORDER.equalsIgnoreCase(split1[0])) {
                    SortOrder sortOrder = SortOrder.valueOf(split1[1].toLowerCase());
                    if (sortOrder.equals(SortOrder.desc)) {
                        sortOrderAsc = false;
                    }
                }
            }
            if (pageNumber == null || pageSize == null || sortBy == null) {
                throw new PagingParseException();
            }
        }
    }

    @Deprecated
    public PageConfig(String pagingParamStr, Integer maxPageSize) {
        this(pagingParamStr);
        if (pageSize > maxPageSize) {
            throw new PagingParseException();
        }
    }

    private PageConfig() {
        rawValue = getDefaultConfig();
    }

    public PageConfig(Long pageNumber, Integer pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.rawValue = getDefaultConfig();
    }

    public static PageConfig limited(@Nullable String pagingParamStr, Integer maxPageSize) {
        PageConfig pageConfig = new PageConfig(pagingParamStr);
        if (pageConfig.getPageSize() > maxPageSize) {
            throw new PagingParseException();
        }
        return pageConfig;
    }

    public static PageConfig defaultConfig() {
        return new PageConfig();
    }

    public String value() {
        return rawValue;
    }

    public long getOffset() {
        return pageNumber * (long) pageSize;
    }

    private String getDefaultConfig() {
        return PAGING_NUM + ":" + pageNumber + "," + PAGING_SIZE + ":" + pageSize + "," + SORT_BY
            +
            ":" + sortBy + "," + SORT_ORDER + ":" + SortOrder.asc.name();
    }

    public PageConfig pageOf(long pageNum) {
        return new PageConfig(pageNum, pageSize);
    }

    enum SortOrder {
        asc,
        desc
    }

    public static class PagingParseException extends RuntimeException {
    }
}
