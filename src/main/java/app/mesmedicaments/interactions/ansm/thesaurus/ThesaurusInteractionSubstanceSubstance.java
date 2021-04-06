package app.mesmedicaments.interactions.ansm.thesaurus;

import java.util.Comparator;
import java.util.stream.Stream;

public final class ThesaurusInteractionSubstanceSubstance
        extends ThesaurusInteraction<ThesaurusSubstance, ThesaurusSubstance> {

    static private Comparator<ThesaurusSubstance> comparator =
            Comparator.comparing(ThesaurusSubstance::getName);

    public ThesaurusInteractionSubstanceSubstance(ThesaurusSubstance substance1,
            ThesaurusSubstance substance2, ThesaurusInteractionLevel level, String description,
            String conduiteATenir) {
        super(Stream.of(substance1, substance2).min(comparator).get(),
                Stream.of(substance1, substance2).max(comparator).get(), level, description,
                conduiteATenir);
    }

}
