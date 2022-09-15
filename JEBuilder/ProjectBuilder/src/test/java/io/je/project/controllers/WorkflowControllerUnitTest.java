package io.je.project.controllers;

import io.je.UnitTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;


/*
@WebMvcTest(WorkflowController.class)
@AutoConfigureBefore
@AutoConfigureAfter
*/
@AutoConfigureMockMvc
@ActiveProfiles("unit-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class WorkflowControllerUnitTest extends UnitTest {

    @InjectMocks
    WorkflowController workflowController;

    @Autowired
    public MockMvc mockMvc;

    private int EXPECTED_RESPONSE = HttpServletResponse.SC_UNAUTHORIZED; // FIXME manage authentication to get : 200 OK


    /******************************************** ADD BLOCKS TESTS *****************************************************/


    @BeforeAll
    public static void init() {

        System.err.println("=== WorkflowControllerUnitTest : init ===");

    }

    @Test
    public void addWorkflowTest() throws Exception {

        System.err.println("=== WorkflowControllerUnitTest : addWorkflowTest : start test ===");

        String model = "{\"projectId\":\"e724d026-9c61-8a35-7aea-b10ccb1f7d92\",\"id\":\"68fbb6f8-9a0b-27be-ddf0-6e05365efdc5\"," +
                "\"name\":\"unitTestWf\",\"description\":\"\",\"onProjectBoot\":false,\"enabled\":true,\"createdBy\":\"administrator\"," +
                "\"modifiedBy\":\"administrator\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/workflow/addWorkflow/").content(model)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder)
        // FIXME auth => .andExpect(status().isOk())
                            .andReturn();

        assertEquals(EXPECTED_RESPONSE, result.getResponse().getStatus(), "=== WorkflowControllerUnitTest : addWorkflowTest : test failed ===");

    }


}
