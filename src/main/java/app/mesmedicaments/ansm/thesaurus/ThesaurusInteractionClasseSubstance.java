package app.mesmedicaments.ansm.thesaurus;

public final class ThesaurusInteractionClasseSubstance
        extends ThesaurusInteraction<ThesaurusClasse, ThesaurusSubstance> {

    public ThesaurusInteractionClasseSubstance(ThesaurusClasse classe,
            ThesaurusSubstance substance, ThesaurusInteractionLevel level, String description,
            String conduiteATenir) {
        super(classe, substance, level, description, conduiteATenir);
    }

}
