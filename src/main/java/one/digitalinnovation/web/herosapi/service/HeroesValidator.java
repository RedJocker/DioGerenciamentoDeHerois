package one.digitalinnovation.web.herosapi.service;

import lombok.RequiredArgsConstructor;
import one.digitalinnovation.web.herosapi.exceptions.HeroesException;
import one.digitalinnovation.web.herosapi.model.Heroes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.util.function.Predicate;

import static one.digitalinnovation.web.herosapi.service.Messages.*;


@Service
@RequiredArgsConstructor
public class HeroesValidator {

    final private HeroesService heroesService;

    final private Predicate<Heroes> verifyNotNullFields = hero -> (hero.getHeroName() != null)
                                                            && (hero.getHeroUniverse() != null)
                                                            && (hero.getHeroFilms() != null);
    final private Predicate<Heroes> verifyNotBlankFields = hero -> !(hero.getHeroName().trim().equals(""))
                                                            && !(hero.getHeroUniverse().trim().equals(""))
                                                            && !(hero.getHeroFilms().trim().equals(""));

    final private Predicate<String> verifyIsNaturalNumber = str -> str.trim().matches("(^+?[1-9][0-9]*$)|(^+?0$)");

    public Heroes validateNotNullFields(Heroes hero) throws HeroesException {

        if(verifyNotNullFields.test(hero)) {
            return hero;
        } else {
            throw new HeroesException(nonNullErrorMessage, HttpStatus.BAD_REQUEST);
        }

    }

    public Heroes validateNotBlankFields(Heroes hero) throws HeroesException {

        if(verifyNotBlankFields.test(hero)) {
            return hero;
        } else {
            throw new HeroesException(nonBlankErrorMessage, HttpStatus.BAD_REQUEST);
        }

    }

    public Heroes validateNaturalNumber(Heroes hero) throws HeroesException{

        if(verifyIsNaturalNumber.test(hero.getHeroFilms())) {
            return hero;
        } else {
            throw new HeroesException(naturalNumberErrorMessage, HttpStatus.BAD_REQUEST);
        }


    }

    public Mono<Heroes> validateUniqueName(Heroes hero) {
        return heroesService.findByName(hero.getHeroName())
                .hasElement()
                .handle((isFound, sink)  -> {
                    if(isFound) {
                        sink.error(new HeroesException(getNonUniqueErrorMessage(hero),HttpStatus.BAD_REQUEST));
                    } else {
                        sink.next(hero);
                    }
                });
    }
}
