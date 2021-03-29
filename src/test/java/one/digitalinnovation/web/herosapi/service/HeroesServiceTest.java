package one.digitalinnovation.web.herosapi.service;

import one.digitalinnovation.web.herosapi.model.Heroes;
import one.digitalinnovation.web.herosapi.repository.HeroesRepository;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HeroesServiceTest {

    List<Heroes> stubRepository = new ArrayList<>( List.of(
            new Heroes("abc", "yes", "yesland", "10"),
            new Heroes("bac", "no", "yesland", "0"),
            new Heroes("cad", "maybe", "yesland", "1"),
            new Heroes("xyz", "outsider", "nowhere", "99")

    ));

    final HeroesRepository repositoryMock = mock(HeroesRepository.class);

    final HeroesService heroesService = new HeroesService(repositoryMock);


    @RepeatedTest(100)
    void findAll() {

        when(repositoryMock.findAll())
                .thenReturn(stubRepository);

        StepVerifier.create(heroesService.findAll())
                .expectNext(new Heroes("abc", "yes", "yesland", "10"))
                .expectNext(new Heroes("bac", "no", "yesland", "0"))
                .expectNext(new Heroes("cad", "maybe", "yesland", "1"))
                .expectNext(new Heroes("xyz", "outsider", "nowhere", "99"))
                .expectComplete()
                .verify();

        when(repositoryMock.findAll())
                .thenReturn(List.of());

        StepVerifier.create(heroesService.findAll())
                .expectComplete();
    }

    @RepeatedTest(100)
    void findAllByUniverse() {

        when(repositoryMock.findAll())
                .thenReturn(stubRepository);

        StepVerifier.create(heroesService.findAllByUniverse("yesland"))
                .expectNext(new Heroes("abc", "yes", "yesland", "10"))
                .expectNext(new Heroes("bac", "no", "yesland", "0"))
                .expectNext(new Heroes("cad", "maybe", "yesland", "1"))
                .expectComplete()
                .verify();

        StepVerifier.create(heroesService.findAllByUniverse(" YESLAND "))
                .expectNext(new Heroes("abc", "yes", "yesland", "10"))
                .expectNext(new Heroes("bac", "no", "yesland", "0"))
                .expectNext(new Heroes("cad", "maybe", "yesland", "1"))
                .expectComplete()
                .verify();

        StepVerifier.create(heroesService.findAllByUniverse("nowhere"))
                .expectNext(new Heroes("xyz", "outsider", "nowhere", "99"))
                .expectComplete()
                .verify();

        StepVerifier.create(heroesService.findAllByUniverse("not a place"))
                .expectComplete()
                .verify();


    }

    @RepeatedTest(100)
    void findById() {
        when(repositoryMock.findById("abc"))
                .thenReturn(stubRepository.stream().filter(hero -> hero.getHeroId().equals("abc")).findFirst());

        StepVerifier.create(heroesService.findById("abc"))
                .expectNext(new Heroes("abc", "yes", "yesland", "10"))
                .expectComplete()
                .verify();

        StepVerifier.create(heroesService.findById("ABC"))
                .expectComplete()
                .verify();

        when(repositoryMock.findById("xyz"))
                .thenReturn(stubRepository.stream().filter(hero -> hero.getHeroId().equals("xyz")).findFirst());

        StepVerifier.create(heroesService.findById("xyz"))
                .expectNext(new Heroes("xyz", "outsider", "nowhere", "99"))
                .expectComplete()
                .verify();
    }

    @RepeatedTest(100)
    void findByName() {
        when(repositoryMock.findAll())
                .thenReturn(stubRepository);

        StepVerifier.create(heroesService.findByName("yes"))
                .expectNext(new Heroes("abc", "yes", "yesland", "10"))
                .expectComplete()
                .verify();

        StepVerifier.create(heroesService.findByName("YES"))
                .expectNext(new Heroes("abc", "yes", "yesland", "10"))
                .expectComplete()
                .verify();

        StepVerifier.create(heroesService.findByName(" outsider "))
                .expectNext(new Heroes("xyz", "outsider", "nowhere", "99"))
                .expectComplete()
                .verify();

        StepVerifier.create(heroesService.findByName("who"))
                .expectComplete()
                .verify();
    }

    @Test
    void save() {

        final Heroes newGuy = new Heroes("ddd", "new guy", "yesland", "1");
        when(repositoryMock.save(newGuy))
                .then(answer -> {
                    stubRepository.add(newGuy);
                    return newGuy; });

        StepVerifier.create(heroesService.save(newGuy))
                .expectNext(newGuy)
                .expectComplete()
                .verify();

        assertTrue(stubRepository.contains(newGuy));

        stubRepository.remove(newGuy);

    }

    @Test
    void deleteById() {

        final Heroes newGuy = new Heroes("ddd", "new guy", "yesland", "1");

        stubRepository.add(newGuy);

        doAnswer(answer -> {
            stubRepository.remove(newGuy);
            return newGuy; }
        ).when(repositoryMock).deleteById("ddd");


        StepVerifier.create(heroesService.deleteById(newGuy.getHeroId()))
                .expectNext(true)
                .expectComplete()
                .verify();

        assertFalse(stubRepository.contains(newGuy));
    }

}