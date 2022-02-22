package io.je;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.Properties;
import io.je.utilities.config.JEConfiguration;
import io.siothconfig.SIOTHConfigUtility;
import io.je.utilities.config.ConfigurationConstants;
import io.siothconfig.SIOTHConfig;
import org.junit.jupiter.api.Test;

public class InjectConfigurationFilesTest {

  @Test
  public void environmentVariableExistsTest() {
    String envVar = System.getenv(ConfigurationConstants.SIOTH_ENVIRONMENT_VARIABLE);
    assertNotNull(envVar);
  }

  @Test
  public void jobEnginePropertiesExistsTest() {
    Properties properties = JEConfiguration.getJobEngineProperties();
    assertNotNull(properties);
    assertNotNull(JEConfiguration.getProjectBuilderUrl());
    assertNotNull(JEConfiguration.getRunnerUrl());
    assertNotNull(JEConfiguration.getMonitorUrl());
    assertNotNull(JEConfiguration.getRunnerLogPath());
    assertNotNull(JEConfiguration.getRunnerLogLevel());
    assertNotNull(JEConfiguration.getZmqSecurity());
    assertNotNull(JEConfiguration.getMonitorPort());
    assertNotNull(JEConfiguration.getSiothId());
    assertNotNull(JEConfiguration.getDevEnvironment());
    assertNotNull(JEConfiguration.getDumpJavaProcess());
    assertNotNull(JEConfiguration.getJavaDumpPath());
    assertNotNull(JEConfiguration.getMonitorLogLevel());
    assertNotNull(JEConfiguration.getMonitorLogPath());
    assertNotNull(JEConfiguration.getBuilderLogPath());
    assertNotNull(JEConfiguration.getBuilderLogLevel());
    assertNotNull(JEConfiguration.getIdentityUrl());
  }

  @Test
  public void SiothConfigJsonTest() {
    String siothId = JEConfiguration.getSiothId();
    assertNotNull(siothId);
    SIOTHConfigUtility.setSiothId(siothId);
    SIOTHConfigUtility.init();
    SIOTHConfig config = SIOTHConfigUtility.getSiothConfig();
    assertNotNull(config);
    assertNotNull(config.getJobEngine());
    assertNotNull(config.getJobEngine().getGeneratedClassesPath());
    assertNotEquals(config.getJobEngine().getCheckHealthEveryMs(), 0);
    assertNotNull(config.getJobEngine().getJeBuilder());
    assertNotNull(config.getJobEngine().getJeRunner());
    assertNotEquals(config.getJobEngine().getLibraryMaxFileSize(), 0);
  }

}
