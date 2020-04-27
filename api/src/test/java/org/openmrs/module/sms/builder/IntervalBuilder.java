package org.openmrs.module.sms.builder;

import org.openmrs.module.sms.TestConstants;
import org.openmrs.module.sms.api.web.Interval;

public class IntervalBuilder extends AbstractBuilder<Interval> {

    private String timeFrom;
    private String timeTo;

    public IntervalBuilder() {
        this.timeFrom = TestConstants.TIME_FROM;
        this.timeTo = TestConstants.TIME_TO;
    }

    @Override
    public Interval build() {
        return new Interval(this.timeFrom, this.timeTo);
    }

    @Override
    public Interval buildAsNew() {
        return build();
    }
}
