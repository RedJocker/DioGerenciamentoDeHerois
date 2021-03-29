package one.digitalinnovation.web.herosapi.controller;

import one.digitalinnovation.web.herosapi.exceptions.HeroesException;
import one.digitalinnovation.web.herosapi.model.Heroes;
import one.digitalinnovation.web.herosapi.model.ResponseModel;
import one.digitalinnovation.web.herosapi.service.HeroesService;
import one.digitalinnovation.web.herosapi.service.HeroesValidator;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

import static one.digitalinnovation.web.herosapi.service.Messages.*;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/heroes")
public class HeroesController {

    private final HeroesService heroesService;
    private final HeroesValidator heroesValidator;

    @GetMapping(value = "", produces = MediaType.TEXT_EVENT_STREAM_VALUE) // no firefox não está funcionando, pede para fazer download ao invés de receber como streaming. No safari e no postman funciona, mas recebe em bloco. No terminal usando curl e no chrome recebe devidamente como streaming
    @ResponseStatus(value = HttpStatus.OK)
    public Flux<ResponseModel> getAllItems() {

        return heroesService.findAll()
                .doOnComplete(() -> log.info("requesting the list off all heroes"))
//                .delayElements(Duration.ofSeconds(3)) // para testes de stream
                .map(ResponseModel::new)
                .switchIfEmpty(Flux.error(new HeroesException(emptyDatabase, HttpStatus.NOT_FOUND)));

    }


    @GetMapping(value = "/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public Mono<ResponseModel> findById(@PathVariable String id) {

        return Mono.just(id)
                .doOnNext(i -> log.info("requesting the hero with id {}", i))
                .flatMap(heroesService::findById)
                .map(ResponseModel::new)
                .switchIfEmpty(Mono.error(new HeroesException(getIdNotFoundMessage(id), HttpStatus.NOT_FOUND)));

    }


    @GetMapping(value = "/universe/{universe}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public Flux<ResponseModel> getAllByUniverse(@PathVariable String universe) {

        return heroesService.findAllByUniverse(universe)
                .doOnComplete(() -> log.info("requesting heroes with universe {}", universe))
                .map(ResponseModel::new)
                .switchIfEmpty(Flux.error(
                        new HeroesException(getUniverseHeroesNotFoundMessage(universe), HttpStatus.NOT_FOUND))
                );

    }


    @GetMapping(value = "/name/{name}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public Mono<ResponseModel> findByName(@PathVariable String name) {

        return heroesService.findByName(name)
                .doOnNext(hero -> log.info("requesting the hero with name {}", name))
                .map(ResponseModel::new)
                .switchIfEmpty(Mono.error(
                        new HeroesException(getHeroWithNameNotFoundMessage(name), HttpStatus.NOT_FOUND))
                );

    }


    @PostMapping(value = "", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public Mono<ResponseModel> createHero(@RequestBody Mono<Heroes> heroMono) {


        return heroMono.doOnNext(hero ->
                log.info("requesting the hero creation with name {}", hero.getHeroName())
            )
            .map(heroesValidator::validateNotNullFields)
            .map(heroesValidator::validateNotBlankFields)
            .map(heroesValidator::validateNaturalNumber)
            .flatMap(heroesValidator::validateUniqueName)
            .flatMap(validHero -> {
                final var heroWithoutId = new Heroes(validHero);
                return heroesService.save(heroWithoutId);
            })
            .map(createdHero -> new ResponseModel(heroCreated, createdHero))
            .doOnNext(hero -> log.info(heroCreated))
            .doOnError(e -> log.info("Error creating hero. " + e.getMessage()));
    }


    @DeleteMapping(value = "/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public Mono<ResponseModel> deleteByID(@PathVariable String id) {
        log.info("Deleting the hero with id {}", id);

        return heroesService.findById(id)
                .doOnNext(hero -> heroesService.deleteById(id))
                .map(hero -> new ResponseModel(heroDeleted, hero))
                .switchIfEmpty(Mono.error(new HeroesException(getIdNotFoundMessage(id), HttpStatus.NOT_FOUND)));

    }

    @DeleteMapping(value = "/name/{name}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public Mono<ResponseModel> deleteByName(@PathVariable String name) {
        log.info("Deleting the hero with name {}", name);

        return heroesService.findByName(name)
                .doOnNext(hero -> heroesService.deleteById(hero.getHeroId()))
                .map(hero -> new ResponseModel(heroDeleted, hero))
                .switchIfEmpty(Mono.error(new HeroesException(getHeroWithNameNotFoundMessage(name), HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/count/")
    public Mono<ResponseEntity<ResponseModel>> countTotal(){
        log.info("requesting total count of heroes");

        return heroesService.findAll()
                .reduceWith(() -> 0, (acc, heroes) -> acc + 1)
                .map(count -> new ResponseEntity<>(
                        new ResponseModel(getCountMessage(count.toString())), HttpStatus.OK));
    }
}
