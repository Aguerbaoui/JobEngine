package io.je;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("unit-test")
public class UnitTest {

    @Autowired
    public MockMvc mockMvc;

    private static AutoCloseable closeable;

    @BeforeAll
    public static void openMocks() {
        System.err.println("=== UnitTest : openMocks ===");

        closeable = MockitoAnnotations.openMocks(UnitTest.class);

        System.err.println("=== UnitTest : mock : LicenseProperties ===");

        //FIXME :
/*
        try (MockedStatic<LicenseProperties> licenseProperties = Mockito.mockStatic(LicenseProperties.class)) {

            licenseProperties.when(LicenseProperties::init).thenThrow(NullPointerException.class);//Return(null);

            licenseProperties.when(LicenseProperties::licenseIsActive).thenReturn(true);

            assertEquals(true, LicenseProperties.licenseIsActive(),
            "=== UnitTest : openMocks : failure : licenseIsActive is not mocked to true");

        }
*/
    }

    @AfterAll
    public static void releaseMocks() throws Exception {
        System.err.println("=== UnitTest : releaseMocks ===");

        closeable.close();
    }

}
