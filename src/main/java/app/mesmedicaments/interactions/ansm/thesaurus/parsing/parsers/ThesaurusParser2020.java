package app.mesmedicaments.interactions.ansm.thesaurus.parsing.parsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
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

    private final StringNormalizer normalizer = new StringNormalizer();

    @Override
    protected Set<ThesaurusInteraction<?, ?>> parseFile(PDDocument document)
            throws ThesaurusParseException {
        final Deque<State> states = new LinkedBlockingDeque<>(4000);
        states.add(new State());
        try {
            final var stripper = new PDFTextStripper() {
                @Override
                protected void writeString(String text, List<TextPosition> textPositions)
                        throws IOException {
                    final var currentState = states.getLast();
                    final var newState = parseLine(text, textPositions, currentState);
                    if (newState != currentState)
                        states.add(newState);
                    // super.writeString(text, textPositions);
                };
            };
            stripper.getText(document);
            return makeInteractionsFromStates(states);
        } catch (IOException e) {
            throw new ThesaurusParseException(e);
        }
    }

    private Set<ThesaurusInteraction<?, ?>> makeInteractionsFromStates(Collection<State> states) {
        final Set<ThesaurusInteraction<?, ?>> interactions = new HashSet<>(2000);
        final Map<String, ThesaurusClasse> classes = extractClassesFromStates(states);
        final Map<String, Map<String, List<State>>> groups = groupAndNormalizeStates(states);
        for (String left : groups.keySet()) {
            for (String right : groups.get(left).keySet()) {
                for (State state : groups.get(left).get(right)) {
                    left = tryToMatchClasse(left, classes.keySet());
                    right = tryToMatchClasse(right, classes.keySet());
                    final boolean leftIsClasse = classes.keySet().contains(left);
                    final boolean rightIsClasse = classes.keySet().contains(right);
                    // TODO: analyser et rectifier les cas où state.level == null
                    // assert state.level != null;
                    final var level = state.level == null ? null
                            : ThesaurusInteractionLevel.values()[state.level];
                    final var description = state.description.toString();
                    final var conduiteATenir = state.conduite.toString();
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

    private String tryToMatchClasse(String name, Set<String> classes) {
        final String n = name.replaceFirst("(?i:AUTRES )", "");
        if (classes.contains(n))
            return n;
        return name;
    }

    private Map<String, Map<String, List<State>>> groupAndNormalizeStates(
            Collection<State> states) {
        final Map<String, Map<String, List<State>>> groups = new HashMap<>(700);
        for (State state : states) {
            if (state.left == null || state.right == null)
                continue;
            groups.computeIfAbsent(state.left, k -> new HashMap<>())
                    .computeIfAbsent(state.right, k -> new ArrayList<>()).add(state);
        }
        for (String left : groups.keySet()) {
            for (String right : groups.get(left).keySet()) {
                // état qui ont les mêmes éléments à gauche et à droite
                final var identical = groups.get(left).get(right);
                // s'ils ont plusieurs descriptions, on choisit la plus longue
                final var bestDescr = identical.stream().map(s -> s.description)
                        .max(Comparator.comparing(StringBuilder::length)).get();
                for (State state : identical) {
                    state.description = bestDescr;
                }
            }
        }
        return groups;
    }

    private Map<String, ThesaurusClasse> extractClassesFromStates(Collection<State> states) {
        final Map<String, Set<String>> classes = new HashMap<>(200);
        for (State state : states) {
            final var compoClasse = state.compoClasse.toString();
            if (!compoClasse.startsWith("("))
                continue;
            final var substances = Arrays.asList(
                    compoClasse.replaceFirst("^\\(", "").replaceFirst("\\)$", "").split(", ?"));
            classes.computeIfAbsent(state.left, k -> new HashSet<>()).addAll(substances);
        }
        return classes.entrySet().stream().collect(
                Collectors.toMap(e -> e.getKey(), e -> new ThesaurusClasse(e.getKey(), e.getValue()
                        .stream().map(ThesaurusSubstance::new).collect(Collectors.toSet()))));
    }

    private State parseLine(String text, List<TextPosition> textPositions, State state) {
        text = normalizer.normalize(text);

        final var t = text; // très agaçant
        if (patternsSkipLine.stream().anyMatch(p -> p.matcher(t).matches())) {
            return state;
        }

        final float size = textPositions.get(0).getFontSize();
        final float sizeInPt = textPositions.get(0).getFontSizeInPt();
        final float xPos = textPositions.get(0).getX();

        if (size == sizeRightSubstance) {
            if (state.right != null && state.level == null && state.description.length() == 0) {
                // il s'agit probablement du nom qui s'étend sur plusieurs lignes
                state.right += " " + text;
            } else {
                final var newState = new State();
                newState.left = state.left;
                newState.compoClasse = state.compoClasse;
                newState.right = text;
                state = newState;
            }
            return state;
        }

        if (size == sizeLeftSubstance) {
            if (state.right != null || state.left == null) {
                state = new State();
                state.left = text;
            } else {
                // sinon il s'agit probablement de la continuité du nom précédent
                state.left += " " + text;
            }
            return state;
        }

        if (sizeInPt == sizeInPtDescription) {
            if (state.right == null) {
                if (text.startsWith("("))
                    state.compoClasse = new StringBuilder(text);
                else if (state.compoClasse.length() > 0)
                    state.compoClasse.append(text);
                return state;
            }
            final var optLevel = IterablesExtensions.getFirstIndexWhere(patternsNiveaux,
                    p -> p.matcher(t).find());
            if (optLevel.isPresent()) {
                final int level = optLevel.get();
                if (state.level != null)
                    // on crée un nouvel état
                    state = state.withLevel(level);
                else
                    // on conserve le même
                    state.level = level;
                state.conduite = new StringBuilder();
                return state;
            }
            if (text.contains("association de deux anti")) {
                // TODO: corriger ce cas
            }
            if (Math.abs(xPos - 97) < Math.abs(xPos - 317)) {
                if (state.description.length() > 0)
                    state.description.append(" ");
                state.description.append(text);
            } else
                state.conduite.append(text);
            return state;
        }
        return state;
    }

}
