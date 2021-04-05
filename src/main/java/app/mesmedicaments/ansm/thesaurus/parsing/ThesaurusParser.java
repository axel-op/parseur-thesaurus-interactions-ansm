package app.mesmedicaments.ansm.thesaurus.parsing;

import java.util.Set;
import app.mesmedicaments.ansm.thesaurus.ThesaurusInteraction;

public abstract class ThesaurusParser {

    public abstract Set<ThesaurusInteraction<?, ?>> parse() throws ThesaurusParseException;

}
