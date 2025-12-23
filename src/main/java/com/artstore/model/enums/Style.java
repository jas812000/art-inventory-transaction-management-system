package com.artstore.model.enums;

/**
 * Represents artistic styles applied to artwork.
 */
public enum Style {

    ABSTRACT("Abstract"),
    SURREALISM("Surrealism"),
    IMPRESSIONISM("Impressionism"),
    POST_IMPRESSIONISM("Post-Impressionism"),
    EXPRESSIONISM("Expressionism"),
    REALISM("Realism"),
    HYPERREALISM("Hyperrealism"),
    CUBISM("Cubism"),
    MINIMALISM("Minimalism"),
    POP_ART("Pop Art"),
    PHOTOREALISM("Photorealism"),
    FAUVISM("Fauvism"),
    BAROQUE("Baroque"),
    ROCOCO("Rococo"),
    RENAISSANCE("Renaissance"),
    NEOCLASSICISM("Neoclassicism"),
    ROMANTICISM("Romanticism"),
    SYMBOLISM("Symbolism"),
    ART_NOUVEAU("Art Nouveau"),
    ART_DECO("Art Deco"),
    FUTURISM("Futurism"),
    CONSTRUCTIVISM("Constructivism"),
    DADAISM("Dadaism"),
    CONCEPTUAL_ART("Conceptual Art"),
    OP_ART("Op Art"),
    NAÏVE_ART("Naïve Art"),
    STREET_ART("Street Art"),
    FOLK_ART("Folk Art"),
    TACHISME("Tachisme"),
    LUMINISM("Luminism"),
    SUPREMATISM("Suprematism"),
    AUTOMATISM("Automatism"),
    COLOR_FIELD_PAINTING("Color Field Painting"),
    HARD_EDGE_PAINTING("Hard Edge Painting"),
    KINETIC_ART("Kinetic Art"),
    GEOMETRIC_ABSTRACTION("Geometric Abstraction"),
    MANGA("Manga"),
    GRAPHIC_NOVEL("Graphic Novel"),
    SKETCH_ART("Sketch Art"),
    FANTASY_ART("Fantasy Art"),
    GOTHIC_ART("Gothic Art"),
    CROSSHATCHING("Crosshatching"),
    SCIENTIFIC_ILLUSTRATION("Scientific Illustration");

    private final String displayName;

    /**
     * Constructs a style with a display-friendly name.
     *
     * @param displayName readable style name
     */
    Style(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the display-friendly style name.
     *
     * @return style name
     */
    @Override
    public String toString() {
        return displayName;
    }
}
