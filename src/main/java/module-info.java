module app.mesmedicaments.interactions.ansm.thesaurus {
    exports app.mesmedicaments.interactions.ansm.thesaurus;
    exports app.mesmedicaments.interactions.ansm.thesaurus.parsing;
    exports app.mesmedicaments.interactions.ansm.thesaurus.parsing.parsers;
    
    requires org.slf4j;
    requires transitive pdfbox;
    requires static lombok;
}
