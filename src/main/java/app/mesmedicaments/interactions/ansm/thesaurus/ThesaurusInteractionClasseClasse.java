package app.mesmedicaments.interactions.ansm.thesaurus;

import java.util.Comparator;
import java.util.stream.Stream;

public final class ThesaurusInteractionClasseClasse
        extends ThesaurusInteraction<ThesaurusClasse, ThesaurusClasse> {

    static private Comparator<ThesaurusClasse> comparator =
            Comparator.comparing(ThesaurusClasse::getName);

    public ThesaurusInteractionClasseClasse(ThesaurusClasse classe1, ThesaurusClasse classe2,
            ThesaurusInteractionLevel level, String description, String conduiteATenir) {
        super(Stream.of(classe1, classe2).min(comparator).get(),
                Stream.of(classe1, classe2).max(comparator).get(), level, description,
                conduiteATenir);
    }

}
