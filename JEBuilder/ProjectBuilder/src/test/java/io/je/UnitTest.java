package io.je;

import io.je.project.config.LicenseProperties;
import io.je.project.controllers.WorkflowController;
import org.junit.After;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
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
