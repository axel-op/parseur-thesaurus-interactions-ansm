package app.mesmedicaments.interactions.ansm.thesaurus.parsing.parsers;

import java.util.Optional;
import java.util.function.Predicate;
import lombok.experimental.PackagePrivate;
import lombok.experimental.UtilityClass;

@UtilityClass
@PackagePrivate
class IterablesExtensions {

    static public <T> Optional<Integer> getFirstIndexWhere(Iterable<T> iterable,
            Predicate<T> predicate) {
        int i = -1;
        for (T el : iterable) {
            i += 1;
            if (predicate.test(el))
                return Optional.of(i);
        }
        return Optional.empty();
    }
}
