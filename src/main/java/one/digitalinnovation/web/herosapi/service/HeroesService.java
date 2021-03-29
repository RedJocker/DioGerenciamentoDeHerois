package one.digitalinnovation.web.herosapi.service;



import lombok.RequiredArgsConstructor;
import one.digitalinnovation.web.herosapi.exceptions.HeroesException;
import one.digitalinnovation.web.herosapi.model.Heroes;
import one.digitalinnovation.web.herosapi.repository.HeroesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Service
public class HeroesService {


    private final HeroesRepository heroesRepository;

    public Flux<Heroes> findAll() {
        return Flux.fromStream(() ->
                StreamSupport.stream(
                        this.heroesRepository.findAll().spliterator(), true
                )
        );
    }

    public Flux<Heroes> findAllByUniverse(String universe) {
        return findAll().filter(heroes -> heroes.getHeroUniverse().equalsIgnoreCase(universe.trim()));

    }

    public Mono<Heroes> findById(String id) {
        return Mono.fromSupplier(() -> this.heroesRepository.findById(id).orElse(null));

    }

    public Mono<Heroes> findByName(String heroName) {
        return findAll().filter(hero -> hero.getHeroName().equalsIgnoreCase(heroName.trim())).singleOrEmpty();
    }

    public Mono<Heroes> save(Heroes hero) {
        return Mono.fromSupplier(() -> this.heroesRepository.save(hero));

    }

    public Mono<Boolean> deleteById(String id) {
        heroesRepository.deleteById(id);
        return Mono.fromSupplier(() -> true);
    }












}
