/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines artistic styles used for artwork classification.
 */
package com.artstore.model.enums;

/**
 * Represents artistic styles applied to artwork.
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

    private final String styleName;

    /**
     * Creates a style with a display-friendly name.
     *
     * @param styleName readable name of the style
     */
    Style(String styleName) {
        this.styleName = styleName;
    }

    /**
     * Returns the display name of the style.
     *
     * @return style name
     */
    public String getStyleName() {
        return styleName;
    }
}
