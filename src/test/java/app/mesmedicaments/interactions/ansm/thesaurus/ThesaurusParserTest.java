package app.mesmedicaments.interactions.ansm.thesaurus;

import static org.junit.jupiter.api.Assertions.*;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import app.mesmedicaments.interactions.ansm.thesaurus.parsing.parsers.ThesaurusParser2020;
import lombok.experimental.ExtensionMethod;
import java.io.IOException;
import java.util.stream.Stream;

@ExtensionMethod(ThesaurusInteractionExtensions.class)
public class ThesaurusParserTest {

    @Test
    public void testThesaurusParser2020() throws IOException {
        final var document = getFile2020();
        final var results = assertDoesNotThrow(() -> new ThesaurusParser2020().parseFile(document));
        assertTrue(!results.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"substances à absorption",
    // TODO
    // "autres médicaments", "autres medicaments",
    // "médicaments administrés"
    })
    public void testClassesConsidereesCommeSubstances(String classe) throws IOException {
        final var doc = getFile2020();
        final var results = assertDoesNotThrow(() -> new ThesaurusParser2020().parseFile(doc));
        assertTrue(results.flatten().stream()
                .flatMap(i -> Stream.of(i.getLeftElement(), i.getRightElement()))
                .map(ThesaurusSubstance::getName).map(String::toLowerCase)
                .noneMatch(s -> s.startsWith(classe)));
    }

    private PDDocument getFile2020() throws IOException {
        final var file = getClass().getResourceAsStream("Thesaurus202010.pdf");
        assert file != null;
        return Loader.loadPDF(file);
    }
}
