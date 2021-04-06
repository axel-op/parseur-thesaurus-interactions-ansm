package app.mesmedicaments.interactions.ansm.thesaurus;

import static org.junit.jupiter.api.Assertions.*;
import org.apache.pdfbox.Loader;
import org.junit.jupiter.api.Test;
import app.mesmedicaments.interactions.ansm.thesaurus.parsing.parsers.ThesaurusParser2020;
import java.io.IOException;

public class ThesaurusParserTest {


    @Test
    public void testThesaurusParser2020() throws IOException {
        final var file = getClass().getResourceAsStream("Thesaurus202010.pdf");
        assert file != null;
        final var document = Loader.loadPDF(file);
        assertDoesNotThrow(() -> new ThesaurusParser2020(document).parse());
    }
}
