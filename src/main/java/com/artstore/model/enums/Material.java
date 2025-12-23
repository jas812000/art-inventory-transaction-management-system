/*
 * This file belongs to the ArtInventoryTransaction application.
 * It defines materials used primarily in sculpture and mixed media.
 */
package com.artstore.model.enums;

/**
 * Represents materials used in sculpture and three-dimensional artwork.
 */
public enum Material {

    STONE("STONE"),
    METAL("METAL"),
    WOOD("WOOD"),
    CLAY("CLAY"),
    CERAMIC("CERAMIC"),
    GLASS("GLASS"),
    RESIN("RESIN"),
    PLASTIC("PLASTIC"),
    WAX("WAX"),
    ICE("ICE"),
    BONE("BONE"),
    PAPER("PAPER"),
    FIBERGLASS("FIBERGLASS"),
    PLASTER("PLASTER"),
    CONCRETE("CONCRETE"),
    FOUND_OBJECTS("FOUND OBJECTS"),
    FOAM("FOAM"),
    TEXTILE("TEXTILE"),
    LEATHER("LEATHER"),
    WIRE("WIRE"),
    SAND("SAND"),
    CARBON_FIBER("CARBON FIBER"),
    THREE_D_PRINTED_MATERIALS("3D PRINTED MATERIALS"),
    BIODEGRADABLE_MATERIALS("BIODEGRADABLE MATERIALS");

    private final String materialName;

    /**
     * Creates a material with a display-friendly name.
     *
     * @param materialName readable name of the material
     */
    Material(String materialName) {
        this.materialName = materialName;
    }

    /**
     * Returns the display name of the material.
     *
     * @return material name
     */
    public String getMaterialName() {
        return materialName;
    }
}
