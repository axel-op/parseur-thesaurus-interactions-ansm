package app.mesmedicaments.ansm.thesaurus;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@EqualsAndHashCode
public class ThesaurusClasseSubstances {

    @Getter
    @NonNull
    private final String name;
    @NonNull
    private final Set<ThesaurusSubstance> substances;

    public ThesaurusClasseSubstances(String name, Collection<ThesaurusSubstance> substances) {
        this.name = name;
        this.substances = new HashSet<>(substances);
    }

    public Set<ThesaurusSubstance> getSubstances() {
        return new HashSet<>(substances);
    }

}
