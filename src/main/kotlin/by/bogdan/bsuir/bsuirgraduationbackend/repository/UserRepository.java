package by.bogdan.bsuir.bsuirgraduationbackend.repository;

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.UserDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @author bogdanshishkin1998@gmail.com
 * @since 4/24/2019
 */
public interface UserRepository extends ReactiveMongoRepository<UserDocument, UUID> {
  Mono<UserDocument> findByUsername(String username);
}
