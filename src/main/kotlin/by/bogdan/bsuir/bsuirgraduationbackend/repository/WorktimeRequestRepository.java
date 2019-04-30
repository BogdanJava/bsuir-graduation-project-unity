package by.bogdan.bsuir.bsuirgraduationbackend.repository;

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
  @Query("{ \"approverId\": ?0, \"approved\": ?1 }")
  Flux<WorktimeRequest> findByApproverIdAndApproved(UUID approverId, Boolean approved);

  Flux<WorktimeRequest> findByApproverId(UUID approverId);

  Mono<Long> countByApproverId(UUID approverId);

  Mono<Long> countByApproverIdAndApproved(UUID approverId, Boolean approved);
}
