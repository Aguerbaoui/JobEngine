package io.je;

import io.je.project.config.LicenseProperties;
import io.je.project.controllers.WorkflowController;
import io.je.utilities.log.JELogger;
import org.junit.After;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


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

        try (MockedStatic<LicenseProperties> licenseProperties = Mockito.mockStatic(LicenseProperties.class)) {

            licenseProperties.when(LicenseProperties::init).thenThrow(NullPointerException.class);//Return(null);

            licenseProperties.when(LicenseProperties::licenseIsActive).thenReturn(true);

            assertEquals(true, LicenseProperties.licenseIsActive(), "licenseIsActive mocked to true");

        }

    }

    @After
    public void releaseMocks() throws Exception {
        closeable.close();
    }
}
