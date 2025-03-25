// This file is part of the ArtInventoryTransaction application, specifically the enums package.
package com.artstore.enums;

/**
 * Enum representing styles used in painting and drawing (e.g., abstract, surrealism, impressionism, realism, etc.).
 */
public enum Style {
    ABSTRACT("ABSTRACT"),
    SURREALISM("SURREALISM"),
    IMPRESSIONISM("IMPRESSIONISM"),
    POST_IMPRESSIONISM("POST IMPRESSIONISM"),
    EXPRESSIONISM("EXPRESSIONISM"),
    REALISM("REALISM"),
    HYPERREALISM("HYPERREALISM"),
    CUBISM("CUBISM"),
    MINIMALISM("MINIMALISM"),
    POP_ART("POP ART"),
    PHOTOREALISM("PHOTOREALISM"),
    FAUVISM("FAUVISM"),
    BAROQUE("BAROQUE"),
    ROCOCO("ROCOCO"),
    RENAISSANCE("RENAISSANCE"),
    NEOCLASSICISM("NEOCLASSICISM"),
    ROMANTICISM("ROMANTICISM"),
    SYMBOLISM("SYMBOLISM"),
    ART_NOUVEAU("ART NOUVEAU"),
    ART_DECO("ART DECO"),
    FUTURISM("FUTURISM"),
    CONSTRUCTIVISM("CONSTRUCTIVISM"),
    DADAISM("DADAISM"),
    CONCEPTUAL_ART("CONCEPTUAL ART"),
    OP_ART("OP ART"),
    NAÏVE_ART("NAÏVE ART"),
    STREET_ART("STREET ART"),
    FOLK_ART("FOLK ART"),
    TACHISME("TACHISME"),
    LUMINISM("LUMINISM"),
    SUPREMATISM("SUPREMATISM"),
    AUTOMATISM("AUTOMATISM"),
    COLOR_FIELD_PAINTING("COLOR FIELD PAINTING"),
    HARD_EDGE_PAINTING("HARD EDGE PAINTING"),
    KINETIC_ART("KINETIC ART"),
    GEOMETRIC_ABSTRACTION("GEOMETRIC ABSTRACTION"),
    MANGA("MANGA"),
    GRAPHIC_NOVEL("GRAPHIC NOVEL"),
    SKETCH_ART("SKETCH ART"),
    FANTASY_ART("FANTASY ART"),
    GOTHIC_ART("GOTHIC ART"),
    CROSSHATCHING("CROSSHATCHING"),
    SCIENTIFIC_ILLUSTRATION("SCIENTIFIC ILLUSTRATION");

    // Holds the name of the style, immutable for each enum constant.
    private final String styleName;

    // Constructor to accept the string value
    Style(String styleName) {
        this.styleName = styleName;
    } // End constructor

    // Getter method to retrieve the style name
    public String getStyleName() {
        return styleName;
    } // End getStyleName method
} // End Enum
