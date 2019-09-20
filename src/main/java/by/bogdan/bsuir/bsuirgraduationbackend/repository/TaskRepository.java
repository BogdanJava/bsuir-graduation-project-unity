package by.bogdan.bsuir.bsuirgraduationbackend.repository;

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TaskDocument;
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TaskStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @author bahdan.shyshkin
 */
public interface TaskRepository extends ReactiveMongoRepository<TaskDocument, UUID> {
  Mono<Integer> countByAssigneeIdAndStatus(UUID assigneeId, TaskStatus status);
}
