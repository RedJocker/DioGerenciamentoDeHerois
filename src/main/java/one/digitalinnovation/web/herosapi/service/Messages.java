package one.digitalinnovation.web.herosapi.service;

import one.digitalinnovation.web.herosapi.model.Heroes;

public class Messages {

    public static final  String naturalNumberErrorMessage =
            "Hero not created, heroFilms must be a natural number";

    public static final  String nonBlankErrorMessage =
            "Hero not created, non blank fields(heroName, heroUniverse, heroFilms) required";

    public static final  String nonNullErrorMessage =
        "Hero not created, non null fields(heroName, heroUniverse, heroFilms) required";

    private static final  String nonUniqueErrorMessageTemplate=
            "Hero not created, already exists. Try GET /heroes/name/%s";

    public static String getNonUniqueErrorMessage(Heroes hero) {
        return String.format(nonUniqueErrorMessageTemplate, hero.getHeroName());
    }

    public static final String emptyDatabase = "No heroes on the database";

    public static final String heroCreated = "Hero created";

    private static final String idNotFoundTemplate = "No hero found with id %s";

    public static String getIdNotFoundMessage(String id) {
        return String.format(idNotFoundTemplate, id);
    }


    private static final String universeHeroesNotFoundTemplate = "No heroes found for universe %s";

    public static String getUniverseHeroesNotFoundMessage(String universe) {
        return String.format(universeHeroesNotFoundTemplate, universe);
    }


    private static final String heroWithNameNotFoundTemplate = "Hero with name %s not found";

    public static String getHeroWithNameNotFoundMessage(String name) {
        return String.format(heroWithNameNotFoundTemplate, name);
    }

    public static final String heroDeleted = "Hero deleted";

    private static final String countTemplate = "There are %s heroes listed";

    public static String getCountMessage(String count) {
        return String.format(countTemplate, count);
    }


}
