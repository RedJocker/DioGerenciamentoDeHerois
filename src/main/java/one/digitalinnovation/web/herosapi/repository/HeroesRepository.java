package one.digitalinnovation.web.herosapi.repository;

import one.digitalinnovation.web.herosapi.model.Heroes;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;


@EnableScan
public interface HeroesRepository extends CrudRepository<Heroes, String> {
}


//DynamoDb does not support ReactiveCrudRepository
