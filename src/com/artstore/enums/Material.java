// This file is part of the ArtInventoryTransaction application, specifically the enums package.
package com.artstore.enums;

/**
 * Enum representing materials used in sculptures (e.g., stone, metal, ceramic, wood, glass, etc.).
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

    // Holds the name of the material type, immutable for each enum constant.
    private final String materialName;

    // Constructor to accept the string value
    Material(String materialName) {
        this.materialName = materialName;
    } // End constructor

    // Getter method to retrieve the material name
    public String getMaterialName() {
        return materialName;
    } // End getMaterialName method
} // End Material Enum
