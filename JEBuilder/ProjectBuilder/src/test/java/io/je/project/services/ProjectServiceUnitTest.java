package io.je.project.services;

import io.je.project.beans.JEProject;
import io.je.utilities.exceptions.LicenseNotActiveException;
import io.je.utilities.exceptions.ProjectLoadException;
import io.je.utilities.exceptions.ProjectNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
class ProjectServiceUnitTest {
    @Autowired
    private ProjectService projectService;

    @Test
    void saveProject() {

    }

    @Test
    void removeProject() {
    }

    @Test
    void getLoadedProjects() {
    }

    @Test
    void getProjectById() throws ProjectNotFoundException, ProjectLoadException, LicenseNotActiveException {
        JEProject test = new JEProject("mock");
        test.setProjectName("testProject");
        test.setState("running");
        Mockito.when(projectService.getProjectById("test"))
                .thenReturn(test);
        JEProject testName = projectService.getProjectById("test");
        Assertions.assertEquals("running", test.getState());
    }

    @Test
    void setLoadedProjects() {
    }

    @Test
    void buildAll() {
    }

    @Test
    void runAll() {
    }

    @Test
    void stopProject() {
    }

    @Test
    void getProject() {
    }

    @Test
    void testSaveProject() {
    }

    @Test
    void projectExists() {
    }

    @Test
    void loadAllProjects() {
    }

    @Test
    void informUser() {
    }

    @Test
    void sendLog() {
    }

    @Test
    void cleanUpHouse() {
    }

    @Test
    void addFile() {
    }
}