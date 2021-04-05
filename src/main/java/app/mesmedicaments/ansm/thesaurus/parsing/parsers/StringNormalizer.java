package app.mesmedicaments.ansm.thesaurus.parsing.parsers;

import java.nio.charset.Charset;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class StringNormalizer {

    static private final Charset charset = Charset.forName("cp1252");

    String normalize(String text) {
        text = text.replaceAll("  +", " ").trim();
        final byte[] original = text.getBytes(charset);
        byte[] normalized = new byte[original.length];
        for (int i = 0; i < original.length; i++) {
            switch (Integer.valueOf(original[i])) {
                // a
                case -32:
                case -30:
                    normalized[i] = 97;
                    break;
                // e
                case -23:
                case -24:
                case -22:
                    normalized[i] = 101;
                    break;
                // i
                case -17:
                case -18:
                    normalized[i] = 105;
                    break;
                // o
                case -12:
                case -10:
                    normalized[i] = 111;
                    break;
                // u
                case -4:
                    normalized[i] = 117;
                    break;
                // œ
                case -100:
                    normalized = Arrays.copyOf(normalized, normalized.length + 1);
                    normalized[i] = 111;
                    normalized[i + 1] = 101;
                    i++;
                    break;
                // apostrophe
                case -110:
                    normalized[i] = 39;
                    break;
                default:
                    normalized[i] = original[i];
            }
        }
        return new String(normalized, charset);
    }

}
