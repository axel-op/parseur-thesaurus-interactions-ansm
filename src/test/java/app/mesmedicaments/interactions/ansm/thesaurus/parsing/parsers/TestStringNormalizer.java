package app.mesmedicaments.interactions.ansm.thesaurus.parsing.parsers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class TestStringNormalizer {

    @Test
    public void testNormalisationApostrophe() {
        final var normalizer = new StringNormalizer();
        assertTrue(normalizer.normalize("’").equals("'"));
    }
    
}
