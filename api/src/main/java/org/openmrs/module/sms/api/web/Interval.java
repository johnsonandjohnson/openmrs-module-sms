package org.openmrs.module.sms.api.web;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.sms.api.util.DateUtil;
import org.openmrs.module.sms.api.web.dto.BaseDTO;

import java.util.Date;

public class Interval extends BaseDTO {

    private static final long serialVersionUID = 7249387385043145349L;
    private Date from;

    private Date to;

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public Interval(String timeFrom, String timeTo) {
        if (StringUtils.isNotBlank(timeFrom)) {
            from = DateUtil.parse(timeFrom, DATE_TIME_PATTERN);
        } else {
            from = new Date(0);
        }
        if (StringUtils.isNotBlank(timeTo)) {
            to = DateUtil.parse(timeTo, DATE_TIME_PATTERN);
        } else {
            to = DateUtil.now();
        }
    }

    public Interval(Date from, Date to) {
        this.from = from;
        this.to = to;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }
}
