/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines artistic techniques used in artwork creation.
 */
package com.artstore.model.enums;

/**
 * Represents artistic techniques used in painting, drawing, and mixed media.
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

    private final String techniqueName;

    /**
     * Creates a technique with a display-friendly name.
     *
     * @param techniqueName readable name of the technique
     */
    Technique(String techniqueName) {
        this.techniqueName = techniqueName;
    }

    /**
     * Returns the display name of the technique.
     *
     * @return technique name
     */
    public String getTechniqueName() {
        return techniqueName;
    }
}
