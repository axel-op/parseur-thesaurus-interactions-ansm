package app.mesmedicaments.interactions.ansm.thesaurus.parsing.parsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import app.mesmedicaments.interactions.ansm.thesaurus.*;
import app.mesmedicaments.interactions.ansm.thesaurus.parsing.*;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(IterablesExtensions.class)
@RequiredArgsConstructor
public class ThesaurusParser2020 extends ThesaurusParser {

    static private final float sizeLeftSubstance = (float) 10;
    static private final float sizeRightSubstance = (float) 8;
    static private final float sizeInPtDescription = (float) 6;

    static private final List<Pattern> patternsNiveaux = Stream
            .of("(a|à) prendre en compte", "pr(e|é)caution d'emploi",
                    "association d(e|é)conseill(e|é)e", "contre(-| )indication")
            .map(r -> "^(- )?" + r).map(r -> Pattern.compile(r, Pattern.CASE_INSENSITIVE))
            .collect(Collectors.toList());

    static private final List<Pattern> patternsSkipLine =
            Stream.of("www\\.ansm.*", "\\d+", "\\+", "\\d+/\\d+", "ANSM.*").map(Pattern::compile)
                    .collect(Collectors.toList());

    private final PDDocument document;
    private final StringNormalizer normalizer = new StringNormalizer();
    private final List<State> states = new ArrayList<>();
    private State currentState = new State();

    @Override
    public Set<ThesaurusInteraction<?, ?>> parse() throws ThesaurusParseException {
        try {
            final var stripper = new PDFTextStripper() {
                @Override
                protected void writeString(String text, List<TextPosition> textPositions)
                        throws IOException {
                    parseLine(text, textPositions);
                    super.writeString(text, textPositions);
                };
            };
            stripper.getText(document);
            states.add(currentState);
            return makeInteractionsFromStates(states);
        } catch (IOException e) {
            throw new ThesaurusParseException(e);
        }
    }

    private Set<ThesaurusInteraction<?, ?>> makeInteractionsFromStates(List<State> states) {
        final Set<ThesaurusInteraction<?, ?>> interactions = new HashSet<>();
        final Map<String, ThesaurusClasse> classes = extractClassesFromStates(states);
        final Map<String, Map<String, List<State>>> groups = groupAndNormalizeStates(states);
        for (String left : groups.keySet()) {
            for (String right : groups.get(left).keySet()) {
                final boolean leftIsClasse = classes.keySet().contains(left);
                final boolean rightIsClasse = classes.keySet().contains(right);
                for (State state : groups.get(left).get(right)) {
                    // TODO: analyser et rectifier les cas où state.level == null
                    // assert state.level != null;
                    final var level = state.level == null ? null
                            : ThesaurusInteractionLevel.values()[state.level];
                    final var description = state.description;
                    final var conduiteATenir = state.conduite;
                    ThesaurusInteraction<?, ?> interaction;
                    if (leftIsClasse && rightIsClasse) {
                        interaction = new ThesaurusInteractionClasseClasse(classes.get(left),
                                classes.get(right), level, description, conduiteATenir);
                    } else if (leftIsClasse || rightIsClasse) {
                        final var classe = classes.get(leftIsClasse ? left : right);
                        final var substance = new ThesaurusSubstance(rightIsClasse ? left : right);
                        interaction = new ThesaurusInteractionClasseSubstance(classe, substance,
                                level, description, conduiteATenir);
                    } else {
                        final var substances = Stream.of(left, right).map(ThesaurusSubstance::new)
                                .collect(Collectors.toList());
                        interaction = new ThesaurusInteractionSubstanceSubstance(substances.get(0),
                                substances.get(1), level, description, conduiteATenir);
                    }
                    interactions.add(interaction);
                }
            }
        }
        return interactions;
    }

    private Map<String, Map<String, List<State>>> groupAndNormalizeStates(List<State> states) {
        final Map<String, Map<String, List<State>>> groups = new HashMap<>();
        for (State state : states) {
            if (state.left == null || state.right == null)
                continue;
            groups.computeIfAbsent(state.left, k -> new HashMap<>())
                    .computeIfAbsent(state.right, k -> new ArrayList<>()).add(state);
        }
        for (String left : groups.keySet()) {
            for (String right : groups.get(left).keySet()) {
                final var identical = groups.get(left).get(right);
                final var bestDescr = identical.stream().map(s -> s.description)
                        .max(Comparator.comparing(String::length)).get();
                for (State state : identical) {
                    state.description = bestDescr;
                }
            }
        }
        return groups;
    }

    private Map<String, ThesaurusClasse> extractClassesFromStates(List<State> states) {
        final Map<String, Set<String>> classes = new HashMap<>();
        for (State state : states) {
            if (!state.compoClasse.startsWith("("))
                continue;
            final var substances = Arrays.asList(state.compoClasse.replaceFirst("^\\(", "")
                    .replaceFirst("\\)$", "").split(", ?"));
            classes.computeIfAbsent(state.left, k -> new HashSet<>()).addAll(substances);
        }
        return classes.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(),
                        e -> new ThesaurusClasse(e.getKey(), e.getValue().stream()
                                .map(ThesaurusSubstance::new).collect(Collectors.toSet()))));
    }

    private void parseLine(String text, List<TextPosition> textPositions) {
        final var textNormalized = normalizer.normalize(text);

        if (patternsSkipLine.stream().anyMatch(p -> p.matcher(textNormalized).matches())) {
            return;
        }

        final float size = textPositions.get(0).getFontSize();
        final float sizeInPt = textPositions.get(0).getFontSizeInPt();
        final float xPos = textPositions.get(0).getX();

        if (size == sizeRightSubstance) {
            states.add(currentState);
            final var newState = new State();
            newState.left = currentState.left;
            newState.compoClasse = currentState.compoClasse;
            currentState = newState;
            currentState.right = textNormalized;
            return;
        }

        if (size == sizeLeftSubstance) {
            states.add(currentState);
            currentState = new State();
            currentState.left = textNormalized;
            return;
        }

        if (sizeInPt == sizeInPtDescription) {
            if (currentState.right == null) {
                if (textNormalized.startsWith("("))
                    currentState.compoClasse += textNormalized;
                return;
            }
            final var optNiveau = IterablesExtensions.getFirstIndexWhere(patternsNiveaux,
                    p -> p.matcher(textNormalized).find());
            if (optNiveau.isPresent()) {
                final int level = optNiveau.get();
                if (currentState.level != null)
                    states.add(currentState);
                currentState = currentState.withLevel(level);
                currentState.conduite = "";
                return;
            }
            if (textNormalized.contains("association de deux anti")) {
                // TODO: corriger ce cas
            }
            if (Math.abs(xPos - 97) < Math.abs(xPos - 317))
                currentState.description += textNormalized;
            else
                currentState.conduite += textNormalized;
            return;
        }

        assert states.isEmpty();
    }

}
