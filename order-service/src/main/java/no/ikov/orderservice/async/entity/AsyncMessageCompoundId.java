package no.ikov.orderservice.async.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class AsyncMessageCompoundId implements Serializable {

    private final String id;
    private final String topic;

}
