package com.dsmc.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public final class PageableHelper {
  private static final int DEFAULT_PAGE_START = 0;
  private static final int DEFAULT_PAGE_SIZE = 100;

  public static Pageable getPageable(Integer page, Integer pageSize) {
    int pageInt = (page == null) ? DEFAULT_PAGE_START : page;
    int pageSizeInt = (pageSize == null) ? DEFAULT_PAGE_SIZE : pageSize;
    return new PageRequest(pageInt, pageSizeInt);
  }
}
