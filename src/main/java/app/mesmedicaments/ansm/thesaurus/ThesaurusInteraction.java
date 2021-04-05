package app.mesmedicaments.ansm.thesaurus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public abstract class ThesaurusInteraction<L, R> {

    @NonNull
    private final L leftElement;
    @NonNull
    private final R rightElement;
    // TODO: rendre non-nullable
    private final ThesaurusInteractionLevel level;
    private final String description;
    private final String conduiteATenir;

}
