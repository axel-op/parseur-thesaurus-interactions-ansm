package app.mesmedicaments.interactions.ansm.thesaurus;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class ThesaurusInteractionExtensions {

    static public Set<ThesaurusInteractionSubstanceSubstance> flatten(
            Set<ThesaurusInteraction<?, ?>> set) {
        return flatten(set.stream());
    }

    static public Set<ThesaurusInteractionSubstanceSubstance> flatten(
            Stream<ThesaurusInteraction<?, ?>> stream) {
        return stream.<ThesaurusInteractionSubstanceSubstance>flatMap(i -> {
            if (i instanceof ThesaurusInteractionClasseClasse)
                return flatten((ThesaurusInteractionClasseClasse) i).stream();
            if (i instanceof ThesaurusInteractionClasseSubstance)
                return flatten((ThesaurusInteractionClasseSubstance) i).stream();
            if (i instanceof ThesaurusInteractionSubstanceSubstance)
                return Stream.of((ThesaurusInteractionSubstanceSubstance) i);
            throw new RuntimeException("Unsupported class: " + i.getClass().getSimpleName());
        }).collect(Collectors.toSet());
    }

    static public Set<ThesaurusInteractionSubstanceSubstance> flatten(
            ThesaurusInteractionClasseSubstance interaction) {
        final var substance = interaction.getRightElement();
        final var level = interaction.getLevel();
        final var description = interaction.getDescription();
        final var conduiteATenir = interaction.getConduiteATenir();
        return interaction.getLeftElement().getSubstances().stream()
                .map(s -> new ThesaurusInteractionSubstanceSubstance(s, substance, level,
                        description, conduiteATenir))
                .collect(Collectors.toSet());
    }

    static public Set<ThesaurusInteractionSubstanceSubstance> flatten(
            ThesaurusInteractionClasseClasse interaction) {
        final var leftClasse = interaction.getLeftElement();
        final var level = interaction.getLevel();
        final var description = interaction.getDescription();
        final var conduiteATenir = interaction.getConduiteATenir();
        return interaction.getRightElement().getSubstances().stream()
                .map(s -> new ThesaurusInteractionClasseSubstance(leftClasse, s, level, description,
                        conduiteATenir))
                .map(ThesaurusInteractionExtensions::flatten).flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

}
