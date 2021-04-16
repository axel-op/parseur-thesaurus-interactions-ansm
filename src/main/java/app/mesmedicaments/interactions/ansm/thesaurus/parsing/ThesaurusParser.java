package app.mesmedicaments.interactions.ansm.thesaurus.parsing;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import app.mesmedicaments.interactions.ansm.thesaurus.ThesaurusInteraction;

public abstract class ThesaurusParser {

    public Set<ThesaurusInteraction<?, ?>> parseFile(InputStream documentInputStream)
            throws ThesaurusParseException, IOException {
        final var document = Loader.loadPDF(documentInputStream);
        return parseFile(document);
    }

    protected abstract Set<ThesaurusInteraction<?, ?>> parseFile(PDDocument document)
            throws ThesaurusParseException;

}
