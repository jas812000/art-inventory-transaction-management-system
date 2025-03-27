package com.tests;

import com.artstore.core.Address;
import com.artstore.exceptions.InvalidTransactionException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    @Test
    void testValidAddressCreation() {
        Address address = new Address("456 Maple St", "Springfield", "IL", 62704);

        assertEquals("456 Maple St", address.getMailingAddress());
        assertEquals("Springfield", address.getCity());
        assertEquals("IL", address.getState());
        assertEquals(62704, address.getZipCode());
    }

    @Test
    void testInvalidStateThrowsException() {
        Exception ex = assertThrows(InvalidTransactionException.class, () ->
                new Address("456 Maple", "City", "Illinois", 62704));
        assertTrue(ex.getMessage().contains("State must be a 2-letter code"));
    }

    @Test
    void testInvalidZipThrowsException() {
        Exception ex = assertThrows(InvalidTransactionException.class, () ->
                new Address("456 Maple", "City", "CA", 123)); // Too short
        assertTrue(ex.getMessage().contains("ZIP Code"));
    }

    @Test
    void testToStringAndFromStringRoundTrip() {
        Address original = new Address("789 Elm", "Oakville", "TX", 76543);
        String serialized = original.toString();
        Address parsed = Address.fromString(serialized);

        assertEquals(original.getMailingAddress(), parsed.getMailingAddress());
        assertEquals(original.getCity(), parsed.getCity());
        assertEquals(original.getState(), parsed.getState());
        assertEquals(original.getZipCode(), parsed.getZipCode());
    }
}


