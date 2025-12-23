package com.artstore.model.enums;

/**
 * Represents materials used in sculpture and three-dimensional artwork.
 */
public enum Material {

    STONE("Stone"),
    METAL("Metal"),
    WOOD("Wood"),
    CLAY("Clay"),
    CERAMIC("Ceramic"),
    GLASS("Glass"),
    RESIN("Resin"),
    PLASTIC("Plastic"),
    WAX("Wax"),
    ICE("Ice"),
    BONE("Bone"),
    PAPER("Paper"),
    FIBERGLASS("Fiberglass"),
    PLASTER("Plaster"),
    CONCRETE("Concrete"),
    FOUND_OBJECTS("Found Objects"),
    FOAM("Foam"),
    TEXTILE("Textile"),
    LEATHER("Leather"),
    WIRE("Wire"),
    SAND("Sand"),
    CARBON_FIBER("Carbon Fiber"),
    THREE_D_PRINTED_MATERIALS("3D Printed Materials"),
    BIODEGRADABLE_MATERIALS("Biodegradable Materials");

    private final String displayName;

    /**
     * Constructs a material with a display-friendly name.
     *
     * @param displayName readable material name
     */
    Material(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the display-friendly material name.
     *
     * @return material name
     */
    @Override
    public String toString() {
        return displayName;
    }
}