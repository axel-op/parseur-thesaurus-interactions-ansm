package app.mesmedicaments.interactions.ansm.thesaurus;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class TestInteractionLevel {

    @Test
    public void testIntValue() {
        int i = 0;
        for (var v : ThesaurusInteractionLevel.values()) {
            i += 1;
            final int intValue = assertDoesNotThrow(() -> v.getValeurSeverite());
            assertEquals(i, intValue);
        }
    }
    
}
