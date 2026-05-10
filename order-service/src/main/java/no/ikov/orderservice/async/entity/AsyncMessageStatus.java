package no.ikov.orderservice.async.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AsyncMessageStatus {
    CREATED("CREATED"), SENT("SENT"), RECEIVED("RECEIVED"), PROCESSED("PROCESSED");

    private final String code;
}
