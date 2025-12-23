package com.artstore.model.enums;

/**
 * Represents artistic techniques used in painting, drawing, and mixed media.
 */
public enum Technique {

    OIL("Oil"),
    ACRYLIC("Acrylic"),
    WATERCOLOR("Watercolor"),
    GOUACHE("Gouache"),
    ENCAUSTIC("Encaustic"),
    TEMPERA("Tempera"),
    FRESCO("Fresco"),
    SPRAY_PAINT("Spray Paint"),
    INK_WASH("Ink Wash"),
    MIXED_MEDIA("Mixed Media"),
    DIGITAL_PAINTING("Digital Painting"),
    GRAPHITE("Graphite"),
    CHARCOAL("Charcoal"),
    COLORED_PENCIL("Colored Pencil"),
    PASTEL("Pastel"),
    INK("Ink"),
    MARKER("Marker"),
    CONTE_CRAYON("Conte Crayon"),
    SGRAFFITO("Sgraffito"),
    AIRBRUSHING("Airbrushing"),
    POINTILLISM("Pointillism"),
    GLAZING("Glazing"),
    IMPASTO("Impasto"),
    DRY_BRUSH("Dry Brush"),
    UNDERPAINTING("Underpainting"),
    ALLA_PRIMA("Alla Prima"),
    GRISAILLE("Grisaille"),
    DRIP_PAINTING("Drip Painting"),
    STIPPLING("Stippling"),
    COLLAGE("Collage"),
    SCUMBLING("Scumbling"),
    TROMPE_LŒIL("Trompe-l'œil"),
    PALETTE_KNIFE_PAINTING("Palette Knife Painting"),
    CROSS_HATCHING("Cross-Hatching"),
    SMUDGING("Smudging"),
    CONTINUOUS_LINE_DRAWING("Continuous Line Drawing"),
    DOODLING("Doodling"),
    CALLIGRAPHY("Calligraphy"),
    LITHOGRAPHY("Lithography"),
    SCRIBBLING("Scribbling"),
    HATCHING("Hatching"),
    BLENDING("Blending");

    private final String displayName;

    /**
     * Constructs a technique with a display-friendly name.
     *
     * @param displayName readable technique name
     */
    Technique(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the display-friendly technique name.
     *
     * @return technique name
     */
    @Override
    public String toString() {
        return displayName;
    }
}
