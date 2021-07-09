# Parseur du thésaurus des interactions médicamenteuses de l'ANSM

L'ANSM (Agence Nationale de Sécurité du Médicament) [fournit sur son site Internet une liste des interactions entre les substances actives](https://ansm.sante.fr/documents/reference/thesaurus-des-interactions-medicamenteuses-1) contenues dans les médicaments vendus en France. Cependant, cette liste est publiée sous forme de fichier PDF difficile à traiter automatiquement.

Cette bibliothèque Java fournit un parseur afin de faciliter l'extraction des informations contenues dans ce fichier.

## Installation

Cette bibliothèque est publiée dans [mon dépôt Maven](https://github.com/axel-op/maven-packages). Suivez [ces instructions](https://github.com/axel-op/maven-packages#readme) pour ajouter un lien vers ce dépôt à votre projet.

Ensuite, la bibliothèque peut être ajoutée à la liste des dépendances du projet Maven, dans le fichier [`pom.xml`](https://maven.apache.org/pom.html#Dependencies) :

```xml
<!-- pom.xml -->
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <!-- ... -->
  <dependencies>
    
    <dependency>
      <groupId>app.mesmedicaments.interactions.ansm</groupId>
      <artifactId>thesaurus</artifactId>
      <version>0.0.1-SNAPSHOT</version>
    </dependency>
    
    <!-- ... -->

  </dependencies>
  <!-- ... -->
</project>
```

## Utilisation

1. Récupérer le fichier PDF :

```java
import java.net.URL;

InputStream file;

// Depuis une ressource locale
var resourceName = "Thesaurus202010.pdf";
file = getClass().getResourceAsStream(resourceName);

// Depuis Internet
var url = "https://ansm.sante.fr/uploads/2020/10/27/20201027-thesaurus-index-des-substances-20102020.pdf";
file = new URL(url).openStream();
```

2. Parser le fichier :

```java
import java.util.Set;
import app.mesmedicaments.interactions.ansm.thesaurus.ThesaurusInteraction;
import app.mesmedicaments.interactions.ansm.thesaurus.parsing.ThesaurusParser;
import app.mesmedicaments.interactions.ansm.thesaurus.parsing.parsers.ThesaurusParser2020;

final ThesaurusParser parser = new ThesaurusParser2020();
final Set<ThesaurusInteraction<?, ?>> interactions = parser.parseFile(file);
```

Les interactions trouvées peuvent être de trois types :

- substance / substance ;
- classe de substances / substance ;
- classe de substances / classe de substances.

Il est possible d'"aplatir" une interaction avec une classe de substances en plusieurs interactions substance / substance, à l'aide des fonctions statiques `flatten` contenues dans [la classe `ThesaurusInteractionExtensions`](src/main/java/app/mesmedicaments/interactions/ansm/thesaurus/ThesaurusInteractionExtensions.java) :

```java
import java.util.stream.*;
import app.mesmedicaments.interactions.ansm.thesaurus.ThesaurusInteractionClasseSubstance;
import app.mesmedicaments.interactions.ansm.thesaurus.ThesaurusInteractionSubstanceSubstance;
import app.mesmedicaments.interactions.ansm.thesaurus.ThesaurusInteractionExtensions;
import app.mesmedicaments.interactions.ansm.thesaurus.ThesaurusSubstance;

// interaction classe de substances / substance
final ThesaurusInteractionClasseSubstance interactionCS = interactions.stream()
    .filter(i -> i instanceof ThesaurusInteractionClasseSubstance)
    .map(i -> (ThesaurusInteractionClasseSubstance) i)
    .findFirst()
    .get();
    
final Set<ThesaurusInteractionSubstanceSubstance> flattened = ThesaurusInteractionExtensions.flatten(interactionCS);

// ça fonctionne aussi sur l'ensemble des résultats :
// final Set<ThesaurusInteractionSubstanceSubstance> flattened = ThesaurusInteractionExtensions.flatten(interactions);

final ThesaurusInteractionSubstanceSubstance interactionSS = flattened.stream().findAny().get();
final ThesaurusSubstance substanceA = interactionSS.getLeftElement();
final ThesaurusSubstance substanceB = interactionSS.getRightElement();

final var names = Stream.of(substanceA, substanceB).map(s -> s.getName()).collect(Collectors.toList());
```
