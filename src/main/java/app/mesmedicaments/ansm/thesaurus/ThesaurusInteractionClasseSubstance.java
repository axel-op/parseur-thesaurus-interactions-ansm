package app.mesmedicaments.ansm.thesaurus;

public final class ThesaurusInteractionClasseSubstance
        extends ThesaurusInteraction<ThesaurusClasseSubstances, ThesaurusSubstance> {

    public ThesaurusInteractionClasseSubstance(ThesaurusClasseSubstances classe,
            ThesaurusSubstance substance, ThesaurusInteractionLevel level, String description,
            String conduiteATenir) {
        super(classe, substance, level, description, conduiteATenir);
    }

}
