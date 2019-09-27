package org.openmrs.module.sms.api.web;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.audit.SmsRecords;
import org.openmrs.module.sms.domain.PagingInfo;

import java.io.Serializable;
import java.util.List;

public class SmsRecordsPageable implements Serializable {

	private static final long serialVersionUID = 4554144138502087590L;

	private Integer pageIndex;

	private Integer pageSize;

	private Integer totalRecords;

	private List<SmsRecord> rows;

	public SmsRecordsPageable(PagingInfo page, SmsRecords smsRecords) {
		this.pageIndex = page.getPage();
		this.pageSize = page.getPageSize();
		this.totalRecords = smsRecords.getCount();
		this.rows = smsRecords.getRecords();
	}

	public Integer getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}

	public List<SmsRecord> getRows() {
		return rows;
	}

	public void setRows(List<SmsRecord> rows) {
		this.rows = rows;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
