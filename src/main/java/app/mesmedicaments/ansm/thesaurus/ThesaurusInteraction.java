package app.mesmedicaments.ansm.thesaurus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public abstract class ThesaurusInteraction<L, R> {

    private final L leftElement;
    private final R rightElement;
    private final ThesaurusInteractionLevel level;
    private final String description;
    private final String conduiteATenir;

}
