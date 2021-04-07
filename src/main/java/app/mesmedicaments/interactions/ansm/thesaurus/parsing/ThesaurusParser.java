package app.mesmedicaments.interactions.ansm.thesaurus.parsing;

import java.util.Set;
import org.apache.pdfbox.pdmodel.PDDocument;
import app.mesmedicaments.interactions.ansm.thesaurus.ThesaurusInteraction;

public abstract class ThesaurusParser {

    public abstract Set<ThesaurusInteraction<?, ?>> parseFile(PDDocument document)
            throws ThesaurusParseException;

}
