package org.openmrs.module.sms.api.templates;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class IncomingTest {

    private static final String EXPECTED_PHONE = "316511515125";
    private static final String VALID_RECIPIENT_MESSAGE = "recipient: " + EXPECTED_PHONE;
    private static final String INVALID_RECIPIENT_MESSAGE = EXPECTED_PHONE;
    private static final String RECIPIENT_REGEX = "recipient: (.*)";
    private static final String SENDER_REGEX = "sender: (.*)";
    private static final String EXPECTED_SENDER = "215661333";
    private static final String VALID_SENDER_MESSAGE = "sender: " + EXPECTED_SENDER;
    private static final String INVALID_SENDER_MESSAGE = EXPECTED_SENDER;

    @Test
    public void extractRecipientShouldReturnExpectedResult() {
        Incoming incoming = new Incoming();
        incoming.setRecipientRegex(RECIPIENT_REGEX);
        String actual = incoming.extractRecipient(VALID_RECIPIENT_MESSAGE);
        assertThat(actual, is(EXPECTED_PHONE));
    }

    @Test
    public void extractRecipientShouldReturnNullWhenInvalidMessage() {
        Incoming incoming = new Incoming();
        incoming.setRecipientRegex(RECIPIENT_REGEX);
        String actual = incoming.extractRecipient(INVALID_RECIPIENT_MESSAGE);
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void extractSenderShouldReturnExpectedResult() {
        Incoming incoming = new Incoming();
        incoming.setSenderRegex(SENDER_REGEX);
        String actual = incoming.extractSender(VALID_SENDER_MESSAGE);
        assertThat(actual, is(EXPECTED_SENDER));
    }

    @Test
    public void extractSenderShouldReturnNullWhenInvalidMessage() {
        Incoming incoming = new Incoming();
        incoming.setSenderRegex(SENDER_REGEX);
        String actual = incoming.extractSender(INVALID_SENDER_MESSAGE);
        assertThat(actual, is(nullValue()));
    }
}