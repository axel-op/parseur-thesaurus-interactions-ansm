package app.mesmedicaments.ansm.thesaurus;

public final class ThesaurusInteractionClasseClasse
        extends ThesaurusInteraction<ThesaurusClasseSubstances, ThesaurusClasseSubstances> {

    public ThesaurusInteractionClasseClasse(ThesaurusClasseSubstances classe1,
            ThesaurusClasseSubstances classe2, ThesaurusInteractionLevel level, String description,
            String conduiteATenir) {
        super(classe1, classe2, level, description, conduiteATenir);
    }

}
