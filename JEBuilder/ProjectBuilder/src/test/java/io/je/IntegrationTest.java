package io.je;

import io.je.project.config.LicenseProperties;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
public class IntegrationTest {
    private AutoCloseable closeable;

    @Before
    public void openMocks() {
        closeable = MockitoAnnotations.openMocks(this);

        Mockito.mockStatic(LicenseProperties.class).when(() -> LicenseProperties.init()).thenReturn(null);

        Mockito.mockStatic(LicenseProperties.class).when(() -> LicenseProperties.licenseIsActive()).thenReturn(true);

    }

    @After
    public void releaseMocks() throws Exception {
        closeable.close();
    }
}
