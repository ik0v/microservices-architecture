package no.ikov.orderservice.async.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AsyncMessageType {

    INBOX("INBOX"), OUTBOX("OUTBOX");

    private final String code;
}
