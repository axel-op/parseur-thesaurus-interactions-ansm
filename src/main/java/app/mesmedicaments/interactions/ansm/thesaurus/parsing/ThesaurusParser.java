package app.mesmedicaments.interactions.ansm.thesaurus.parsing;

import java.util.Set;
import app.mesmedicaments.interactions.ansm.thesaurus.ThesaurusInteraction;

public abstract class ThesaurusParser {

    public abstract Set<ThesaurusInteraction<?, ?>> parse() throws ThesaurusParseException;

}
