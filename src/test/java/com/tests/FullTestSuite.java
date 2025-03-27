package com.tests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        ArtTest.class,
        PrintTest.class,
        PaintingTest.class,
        DrawingTest.class,
        SculptureTest.class,
        CustomerTest.class,
        AddressTest.class,
        TransactionManagerTest.class,
        ArtInventoryManagerTest.class,
        ExceptionIntegrationTest.class
})
public class FullTestSuite {
}
