package app.mesmedicaments.interactions.ansm.thesaurus;

public enum ThesaurusInteractionLevel {
    /**
     * A prendre en compte
     */
    APEC,
    /**
     * Précaution d'emploi
     */
    PE,
    /**
     * Association déconseillée
     */
    AD,
    /**
     * Contre-indication
     */
    CI;

    public int intValue() {
        final var values = ThesaurusInteractionLevel.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i] == this)
                return i;
        }
        throw new IllegalArgumentException(
                "This should never happen. Cannot find int value for " + this.toString());
    }
}
