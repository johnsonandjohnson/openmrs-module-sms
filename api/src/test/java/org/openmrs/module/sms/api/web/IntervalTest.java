package org.openmrs.module.sms.api.web;

import org.junit.Test;
import org.openmrs.module.sms.api.util.DateUtil;
import org.openmrs.module.sms.builder.IntervalBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class IntervalTest {

  @Test
  public void intervalFunctionality() {
    Interval createIntervalUsingString = new Interval("", "");
    Interval createIntervalUsingDate = new Interval(new Date(0), DateUtil.now());
    assertThat(createIntervalUsingString.getFrom(), is(createIntervalUsingDate.getFrom()));
    assertThat(createIntervalUsingString.getTo(), is(createIntervalUsingDate.getTo()));
  }

  @Test
  public void setAndGetFunctionality() {
    Date from = createDate(2010, Calendar.NOVEMBER, 16, 15, 43, 59, "UTC");
    Date to = createDate(2011, Calendar.NOVEMBER, 17, 15, 43, 59, "UTC");
    Interval interval = new IntervalBuilder().build();
    interval.setFrom(from);
    interval.setTo(to);
    assertThat(interval.getFrom(), is(from));
    assertThat(interval.getTo(), is(to));
  }

  private Date createDate(
      int year, int month, int day, int hour, int minute, int second, String timezone) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month, day, hour, minute, second);
    calendar.set(Calendar.MILLISECOND, 0);
    calendar.setTimeZone(TimeZone.getTimeZone(timezone));
    return calendar.getTime();
  }
}
