package by.bogdan.bsuir.bsuirgraduationbackend.repository;

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.ProjectDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * @author bahdan.shyshkin
 */
public interface ProjectRepository extends ReactiveMongoRepository<ProjectDocument, UUID> {
  Flux<ProjectDocument> findByAssignedPersonsIdsIn(List<UUID> userIds);
  Mono<ProjectDocument> findByName(String name);
}
