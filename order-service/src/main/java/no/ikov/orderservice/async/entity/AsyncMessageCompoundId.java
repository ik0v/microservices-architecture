package no.ikov.orderservice.async.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AsyncMessageCompoundId implements Serializable {

    private String id;
    private String topic;

}
