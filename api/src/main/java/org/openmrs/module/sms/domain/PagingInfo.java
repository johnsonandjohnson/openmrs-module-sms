package org.openmrs.module.sms.domain;

import org.openmrs.module.sms.api.web.dto.BaseDTO;

public class PagingInfo extends BaseDTO {

    private static final long serialVersionUID = 247438117875000891L;
    private int page;
    private int pageSize;
    private Long totalRecordCount;
    private boolean loadRecordCount;

    public PagingInfo() {
    }

    /**
     * Creates a new {@link PagingInfo} instance.
     *
     * @param page     The 1-based number of the page being requested.
     * @param pageSize The number of records to include on each page.
     */
    public PagingInfo(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;

        this.loadRecordCount = true;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotalRecordCount() {
        return totalRecordCount;
    }

    public void setTotalRecordCount(Long totalRecordCount) {
        this.totalRecordCount = totalRecordCount;

        // If the total records is set to anything other than null, than don't reload the count
        this.loadRecordCount = totalRecordCount == null;
    }

    public boolean shouldLoadRecordCount() {
        return loadRecordCount;
    }

    public void setLoadRecordCount(boolean loadRecordCount) {
        this.loadRecordCount = loadRecordCount;
    }
}
