package by.bogdan.bsuir.bsuirgraduationbackend.repository;

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.RequestStatus;
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.WorktimeRequest;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @author bahdan.shyshkin
 */
public interface WorktimeRequestRepository extends ReactiveMongoRepository<WorktimeRequest, UUID> {
  @Query("{ \"approverId\": ?0, \"status\": ?1 }")
  Flux<WorktimeRequest> findByApproverIdAndStatus(UUID approverId, RequestStatus status);

  Flux<WorktimeRequest> findByApproverId(UUID approverId);

  Mono<Long> countByApproverId(UUID approverId);

  Mono<Long> countByApproverIdAndStatus(UUID approverId, RequestStatus status);
}
