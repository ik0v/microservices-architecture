package no.ikov.orderservice.async.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "async_messages")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "topic"}, callSuper = false)
@IdClass(AsyncMessageCompoundId.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsyncMessage extends PersistableEntity<AsyncMessageCompoundId> {

    @Id
    @Column(nullable = false)
    private String id;

    @Id
    @Column(nullable = false)
    private String topic;

    private String headers;

    @Column(name = "message_value", nullable = false)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AsyncMessageType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AsyncMessageStatus status;

    @Override
    @Transient
    public AsyncMessageCompoundId getId() {
        return new AsyncMessageCompoundId(id, topic);
    }

}
