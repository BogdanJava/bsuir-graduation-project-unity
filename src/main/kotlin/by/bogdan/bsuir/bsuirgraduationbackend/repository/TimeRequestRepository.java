package by.bogdan.bsuir.bsuirgraduationbackend.repository;

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TimeRequest;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @author bogdanshishkin1998@gmail.com
 * @since 4/24/2019
 */
public interface TimeRequestRepository extends ReactiveMongoRepository<TimeRequest, UUID> {
  @Query("{ \"approverId\": ?0, \"approved\": ?1 }")
  Flux<TimeRequest> findByApproverIdAndApproved(UUID approverId, Boolean approved);

  Flux<TimeRequest> findByApproverId(UUID approverId);

  Mono<Long> countByApproverId(UUID approverId);

  Mono<Long> countByApproverIdAndApproved(UUID approverId, Boolean approved);
}
