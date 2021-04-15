package app.mesmedicaments.interactions.ansm.thesaurus.parsing.parsers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import lombok.experimental.PackagePrivate;

@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@With
@PackagePrivate
class State {

    String left = null;
    String right = null;
    Integer level = null;
    StringBuilder description = new StringBuilder();
    StringBuilder conduite = new StringBuilder();
    StringBuilder compoClasse = new StringBuilder();

}
