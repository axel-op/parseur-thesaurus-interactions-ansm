package app.mesmedicaments.interactions.ansm.thesaurus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(ThesaurusInteractionExtensions.class)
public class ThesaurusInteractionExtensionsTest {

    @Test
    public void testExtensions() {
        final var dummySubs = Stream.of("a", "b", "c", "d").map(ThesaurusSubstance::new)
                .collect(Collectors.toList());
        final var dummyClasse = new ThesaurusClasse("classe", dummySubs.subList(0, 3));
        final var sub = dummySubs.get(3);
        final var level = ThesaurusInteractionLevel.APEC;
        final var dummyInt =
                new ThesaurusInteractionClasseSubstance(dummyClasse, sub, level, "", "");
        final var flattened = dummyInt.flatten();
        final var expected = dummyClasse.getSubstances().stream()
                .map(s -> new ThesaurusInteractionSubstanceSubstance(s, sub, level, "", ""))
                .collect(Collectors.toSet());
        assertEquals(expected, flattened);
    }

}
