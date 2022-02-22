package io.je;


import io.je.project.controllers.ProjectController;
import io.je.project.services.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerTest {

    @InjectMocks
    ProjectController projectController;

    @Mock
    ProjectService projectService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getProjectRunStatus() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/jeproject/getProjectRunStatus/e724d026-9c61-8a35-7aea-b10ccb1f7d92")
                .accept(MediaType.APPLICATION_JSON_VALUE);
         mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isOk()).andExpect(content().string("false"));
    }
}
