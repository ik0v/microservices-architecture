package no.ikov.orderservice.async.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.domain.Persistable;

import java.time.OffsetDateTime;

@MappedSuperclass
public abstract class PersistableEntity<T> implements Persistable<T> {

    @Column(insertable = false, updatable = false)
    @ColumnDefault("now()")
    private OffsetDateTime createdAt;

    @Override
    public boolean isNew() {
        return false;
    }
}
