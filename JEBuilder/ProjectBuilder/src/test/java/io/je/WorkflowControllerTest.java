package io.je;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.project.controllers.WorkflowController;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WorkflowControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @InjectMocks
    WorkflowController workflowController;
    @Autowired
    private MockMvc mockMvc;

    /******************************************** ADD BLOCKS TESTS *****************************************************/
    @Test
    public void addWorkflowTest() throws Exception {
        String model = "{\"projectId\":\"e724d026-9c61-8a35-7aea-b10ccb1f7d92\",\"id\":\"68fbb6f8-9a0b-27be-ddf0-6e05365efdc5\"," +
                "\"name\":\"unitTestWf\",\"description\":\"\",\"onProjectBoot\":false,\"enabled\":true,\"createdBy\":\"administrator\"," +
                "\"modifiedBy\":\"administrator\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/workflow/addWorkflow/").content(model)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        //JEResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), JEResponse.class);
        assertEquals(result.getResponse().getStatus(), 200);
    }


}
