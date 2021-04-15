package app.mesmedicaments.interactions.ansm.thesaurus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ThesaurusInteractionLevel {
    /**
     * A prendre en compte
     */
    APEC(1),
    /**
     * Précaution d'emploi
     */
    PE(2),
    /**
     * Association déconseillée
     */
    AD(3),
    /**
     * Contre-indication
     */
    CI(4);

    @Getter
    private final int valeurSeverite;

}
