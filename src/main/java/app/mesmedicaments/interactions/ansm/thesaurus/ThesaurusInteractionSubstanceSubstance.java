package app.mesmedicaments.interactions.ansm.thesaurus;

public final class ThesaurusInteractionSubstanceSubstance
        extends ThesaurusInteraction<ThesaurusSubstance, ThesaurusSubstance> {

    public ThesaurusInteractionSubstanceSubstance(ThesaurusSubstance substance1,
            ThesaurusSubstance substance2, ThesaurusInteractionLevel level, String description,
            String conduiteATenir) {
        super(substance1, substance2, level, description, conduiteATenir);
    }

}
