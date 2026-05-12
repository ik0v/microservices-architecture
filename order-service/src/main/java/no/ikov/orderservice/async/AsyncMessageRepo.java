package no.ikov.orderservice.async;

import no.ikov.orderservice.async.entity.AsyncMessage;
import no.ikov.orderservice.async.entity.AsyncMessageCompoundId;
import no.ikov.orderservice.async.entity.AsyncMessageStatus;
import no.ikov.orderservice.async.entity.AsyncMessageType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsyncMessageRepo extends JpaRepository<AsyncMessage, AsyncMessageCompoundId> {

    List<AsyncMessage> findByTypeAndStatus(AsyncMessageType type, AsyncMessageStatus status, Pageable pageable);
}
