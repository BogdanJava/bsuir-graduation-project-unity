package by.bogdan.bsuir.bsuirgraduationbackend.repository;

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TimelineEvent;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.util.UUID;

/**
 * @author bahdan.shyshkin
 */
public interface TimelineEventRepository extends ReactiveMongoRepository<TimelineEvent, UUID> {
}
