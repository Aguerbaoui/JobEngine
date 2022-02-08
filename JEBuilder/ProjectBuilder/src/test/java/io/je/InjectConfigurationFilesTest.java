package io.je;

import static org.junit.Assert.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import io.je.utilities.config.ConfigurationConstants;
import io.siothconfig.SIOTHConfig;
import io.siothconfig.SIOTHConfigurationConstants;
import utils.string.StringUtilities;
/*
public class InjectConfigurationFilesTest {

  String envVar = "";

  @Test
  public void environmentVariableExistsTest() {
    envVar = System.getenv(ConfigurationConstants.SIOTH_ENVIRONMENT_VARIABLE);
    System.out.println("Environment variable detected: " + envVar + "\n");
    assertFalse(StringUtilities.isEmpty(envVar));
  }

  @Test
  public void jobEnginePropertiesExistsTest() {
    environmentVariableExistsTest();
    String path = envVar + "\\JobEngine\\jobengine.properties";
    Properties configProps = new Properties();
    try {
      InputStream inputStream = new FileInputStream(path);
      assertNotNull(inputStream);

      configProps.load(inputStream);
      System.out.println("Found properties: \n");
      configProps.forEach((key, value) -> {
        System.out.println(key + " => " + value);
      });

    } catch (IOException e) {
      assertEquals("Exception thrown", true);
    }
  }
*/
  /*@Test
  public void SiothConfigJsonTest() {
    environmentVariableExistsTest();
    String configPath = SIOTHConfigurationConstants.SIOTH_JSON_CONFIG;
    assertNotNull(configPath);
    try {
      ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      String file = configPath;
      String json = new String(Files.readAllBytes(Paths.get(file)));
      assertNotNull(json);
      SIOTHConfig siothConfig = objectMapper.readValue(json, SIOTHConfig.class);
      assertNotNull(siothConfig);

    } catch (Exception e) {
      assertEquals("Exception thrown for missing json values" + e.getLocalizedMessage(), true);
    }
  }*/
//}
