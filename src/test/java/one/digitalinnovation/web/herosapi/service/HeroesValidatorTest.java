package one.digitalinnovation.web.herosapi.service;

import one.digitalinnovation.web.herosapi.exceptions.HeroesException;
import one.digitalinnovation.web.herosapi.model.Heroes;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static one.digitalinnovation.web.herosapi.service.Messages.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HeroesValidatorTest {

    public HeroesValidator initValidator() {
        final HeroesService serviceMock = mock(HeroesService.class);

        when(serviceMock.findByName("existing name"))
                .thenReturn(Mono.just(
                        new Heroes("id",
                                "existing name",
                                "universe",
                                "number of films")
                ));

        when(serviceMock.findByName("non existing name"))
                .thenReturn(Mono.empty());

        return  new HeroesValidator(serviceMock);
    }

    @Test
    public void verifyNotNullValidations() throws Exception {

        final HeroesValidator heroesValidator = initValidator();

        final Heroes hero = new Heroes();

        Assert.assertNull(hero.getHeroId());
        Assert.assertNull(hero.getHeroName());
        Assert.assertNull(hero.getHeroUniverse());
        Assert.assertNull(hero.getHeroFilms());

        final Mono<Heroes> heroMono = Mono.just(hero)
                .map(heroesValidator::validateNotNullFields);

        StepVerifier
                .create(heroMono)
                .expectError(HeroesException.class)
                .verify();

        StepVerifier
                .create(heroMono)
                .expectErrorMessage(nonNullErrorMessage)
                .verify();

    }

    @Test
    public void verifyNotBlankValidations() throws Exception {

        final HeroesValidator heroesValidator = initValidator();

        final Heroes hero = new Heroes();

        hero.setHeroName("");
        hero.setHeroFilms("1");
        hero.setHeroUniverse("a");

        final Mono<Heroes> heroMono = Mono.just(hero)
                .map(heroesValidator::validateNotBlankFields);

        StepVerifier
                .create(heroMono)
                .expectError(HeroesException.class)
                .verify();

        StepVerifier
                .create(heroMono)
                .expectErrorMessage(nonBlankErrorMessage)
                .verify();

    }

    @Test
    public void verifyNumberFieldValidations() throws Exception {

        final HeroesValidator heroesValidator = initValidator();

        final Heroes incorrectHero =
                new Heroes("id", "name", "universe", "number of films");

        final Mono<Heroes> incorrectHeroMono = Mono.just(incorrectHero)
                .map(heroesValidator::validateNaturalNumber);

        StepVerifier
                .create(incorrectHeroMono)
                .expectError(HeroesException.class)
                .verify();

        StepVerifier
                .create(incorrectHeroMono)
                .expectErrorMessage(naturalNumberErrorMessage)
                .verify();

        final Heroes negativeHero =
                new Heroes("id", "name", "universe", "-10");

        final Mono<Heroes> negativeHeroMono = Mono.just(negativeHero)
                .map(heroesValidator::validateNaturalNumber);

        StepVerifier
                .create(negativeHeroMono)
                .expectError(HeroesException.class)
                .verify();

        StepVerifier
                .create(negativeHeroMono)
                .expectErrorMessage(naturalNumberErrorMessage)
                .verify();

        final Heroes shyHero =
                new Heroes("id", "name", "universe", "0");

        final Mono<Heroes> shyHeroMono = Mono.just(shyHero)
                .map(heroesValidator::validateNaturalNumber);

        StepVerifier
                .create(shyHeroMono)
                .expectNext(shyHero)
                .verifyComplete();
        
        final Heroes correctHero =
                new Heroes("id", "name", "universe", "10");

        final Mono<Heroes> correctHeroMono = Mono.just(correctHero)
                .map(heroesValidator::validateNaturalNumber);

        StepVerifier
                .create(correctHeroMono)
                .expectNext(correctHero)
                .verifyComplete();
    }

    @Test
    public void verifyUniqueNameValidations() throws Exception {

        final HeroesValidator heroesValidator = initValidator();

        final Heroes existingHero =
                new Heroes("id", "existing name", "universe", "number of films");

        StepVerifier.create(heroesValidator.validateUniqueName(existingHero))
                .expectError(HeroesException.class)
                .verify();

        StepVerifier.create(heroesValidator.validateUniqueName(existingHero))
                .expectErrorMessage(getNonUniqueErrorMessage(existingHero))
                .verify();

        final Heroes nonExistingHero =
                new Heroes("id", "non existing name", "universe", "number of films");

        StepVerifier.create(heroesValidator.validateUniqueName(nonExistingHero))
                .expectNextCount(1)
                .verifyComplete();

    }

    @Test
    public void testCopyWithoutIdConstructor() throws Exception {
        final HeroesValidator heroesValidator = initValidator();

        final Heroes hero =
                new Heroes("id", "name", "universe", "number of films");

        final Heroes copy = new Heroes(hero);

        Assert.assertNull(copy.getHeroId());
        Assert.assertEquals(copy.getHeroName(), "name");
        Assert.assertEquals(copy.getHeroUniverse(), "universe");
        Assert.assertEquals(copy.getHeroFilms(), "number of films");

    }

}