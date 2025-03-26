// This file is part of the ArtInventoryTransaction application, specifically the enums package.
package com.artstore.enums;

/**
 * Enum representing techniques used in painting and drawing (e.g., oil, acrylic, watercolor, pencil, charcoal, etc.).
 */
public enum Technique {
    OIL("OIL"),
    ACRYLIC("ACRYLIC"),
    WATERCOLOR("WATERCOLOR"),
    GOUACHE("GOUACHE"),
    ENCAUSTIC("ENCAUSTIC"),
    TEMPERA("TEMPERA"),
    FRESCO("FRESCO"),
    SPRAY_PAINT("SPRAY PAINT"),
    INK_WASH("INK WASH"),
    MIXED_MEDIA("MIXED MEDIA"),
    DIGITAL_PAINTING("DIGITAL PAINTING"),
    GRAPHITE("GRAPHITE"),
    CHARCOAL("CHARCOAL"),
    COLORED_PENCIL("COLORED PENCIL"),
    PASTEL("PASTEL"),
    INK("INK"),
    MARKER("MARKER"),
    CONTE_CRAYON("CONTE CRAYON"),
    SGRAFFITO("SGRAFFITO"),
    AIRBRUSHING("AIRBRUSHING"),
    POINTILLISM("POINTILLISM"),
    GLAZING("GLAZING"),
    IMPASTO("IMPASTO"),
    DRY_BRUSH("DRY BRUSH"),
    UNDERPAINTING("UNDERPAINTING"),
    ALLA_PRIMA("ALLA PRIMA"),
    GRISAILLE("GRISAILLE"),
    DRIP_PAINTING("DRIP PAINTING"),
    STIPPLING("STIPPLING"),
    COLLAGE("COLLAGE"),
    SCUMBLING("SCUMBLING"),
    TROMPE_LŒIL("TROMPE-L'ŒIL"),
    PALETTE_KNIFE_PAINTING("PALETTE KNIFE PAINTING"),
    CROSS_HATCHING("CROSS-HATCHING"),
    SMUDGING("SMUDGING"),
    CONTINUOUS_LINE_DRAWING("CONTINUOUS LINE DRAWING"),
    DOODLING("DOODLING"),
    CALLIGRAPHY("CALLIGRAPHY"),
    LITHOGRAPHY("LITHOGRAPHY"),
    SCRIBBLING("SCRIBBLING"),
    HATCHING("HATCHING"),
    BLENDING("BLENDING");

    // Holds the name of the technique, immutable for each enum constant.
    private final String techniqueName;

    // Constructor to accept the string value
    Technique(String techniqueName) {
        this.techniqueName = techniqueName;
    } // End constructor

    // Getter method to retrieve the technique name
    public String getTechniqueName() {
        return techniqueName;
    } // End getTechniqueName method

} // End Technique Enum
