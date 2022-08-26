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
public class AddWorkflowBlocksTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @InjectMocks
    WorkflowController workflowController;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void addStartBlockTest() throws Exception {
        String model = "{\"id\":\"b-075c-cbaa-1658-9ccf\",\"type\":\"start\",\"attributes\":{\"name\":\"Start1\",\"description\":\"\"}," +
                "\"projectId\":\"e724d026-9c61-8a35-7aea-b10ccb1f7d92\",\"workflowId\":\"68fbb6f8-9a0b-27be-ddf0-6e05365efdc5\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/workflow/addWorkflowBlock/").content(model)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        //JEResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), JEResponse.class);
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void addEndBlockTest() throws Exception {
        String model = "{\"id\":\"b-bb25-d314-4a39-a8a4\",\"type\":\"end\",\"attributes\":{\"name\":\"End0.\",\"description\":\"\"}," +
                "\"projectId\":\"e724d026-9c61-8a35-7aea-b10ccb1f7d92\",\"workflowId\":\"68fbb6f8-9a0b-27be-ddf0-6e05365efdc5\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/workflow/addWorkflowBlock/").content(model)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        //JEResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), JEResponse.class);
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void addScheduledStartBlockTest() throws Exception {
        String model = "{\"id\":\"b-600d-3558-2f26-6df3\",\"type\":\"start\",\"attributes\":{\"name\":\"Start2\",\"description\":\"\"," +
                "\"enddate\":\"2022-03-01T15:00:39.953Z\",\"eventType\":\"cycletimerevent\"," +
                "\"occurrences\":30},\"projectId\":\"e724d026-9c61-8a35-7aea-b10ccb1f7d92\",\"workflowId\":\"68fbb6f8-9a0b-27be-ddf0-6e05365efdc5\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/workflow/addWorkflowBlock/").content(model)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        //JEResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), JEResponse.class);
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void addScriptBlockTest() throws Exception {
        String model = "{\"id\":\"b-2036-7b2d-44bc-09cd\",\"type\":\"scripttask\",\"attributes\":{\"name\":\"Script\",\"description\":\"\"," +
                "\"imports\":[\"org.apache.commons.lang3.StringUtils\"],\"script\":\"\",\"timeout\":600,\"autoStoreVariablesInScript\":\"\"}," +
                "\"projectId\":\"e724d026-9c61-8a35-7aea-b10ccb1f7d92\",\"workflowId\":\"68fbb6f8-9a0b-27be-ddf0-6e05365efdc5\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/workflow/addWorkflowBlock/").content(model)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        //JEResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), JEResponse.class);
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void addDBEditBlockTest() throws Exception {
        String model = "{\"id\":\"b-607a-7bf0-5919-93e2\",\"type\":\"dbeditservicetask\",\"attributes\":{\"name\":\"DB Edit\"," +
                "\"description\":\"\",\"request\":\"\"}," +
                "\"projectId\":\"e724d026-9c61-8a35-7aea-b10ccb1f7d92\",\"workflowId\":\"68fbb6f8-9a0b-27be-ddf0-6e05365efdc5\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/workflow/addWorkflowBlock/").content(model)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        //JEResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), JEResponse.class);
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void addDBInsertBlockTest() throws Exception {
        String model = "{\"id\":\"b-0e60-e13a-05b5-f27a\",\"type\":\"dbwriteservicetask\",\"attributes\":{\"name\":\"DB Insert\",\"description\":\"\",\"request\":\"\"},\"projectId\":\"e724d026-9c61-8a35-7aea-b10ccb1f7d92\",\"workflowId\":\"68fbb6f8-9a0b-27be-ddf0-6e05365efdc5\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/workflow/addWorkflowBlock/").content(model)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        //JEResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), JEResponse.class);
        assertEquals(200, result.getResponse().getStatus());
    }


    @Test
    public void addDBReadBlockTest() throws Exception {
        String model = "{\"id\":\"b-d0b5-5c3f-6399-6ba3\",\"type\":\"dbreadservicetask\",\"attributes\":{\"name\":\"DB Read\",\"description\":\"\",\"request\":\"\"},\"projectId\":\"e724d026-9c61-8a35-7aea-b10ccb1f7d92\",\"workflowId\":\"68fbb6f8-9a0b-27be-ddf0-6e05365efdc5\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/workflow/addWorkflowBlock/").content(model)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        //JEResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), JEResponse.class);
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void addWebApiBlockTest() throws Exception {
        String model = "{\"id\":\"b-593e-ee8f-09e3-e82b\",\"type\":\"webtask\",\"attributes\":{\"name\":\"Web Task\",\"description\":\"\"},\"projectId\":\"e724d026-9c61-8a35-7aea-b10ccb1f7d92\",\"workflowId\":\"68fbb6f8-9a0b-27be-ddf0-6e05365efdc5\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/workflow/addWorkflowBlock/").content(model)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        //JEResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), JEResponse.class);
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void addInformBlockTest() throws Exception {
        String model = "{\"id\":\"b-c9c2-45c7-effc-2c56\",\"type\":\"inform\",\"attributes\":{\"name\":\"Inform\",\"description\":\"\",\"message\":\"\"},\"projectId\":\"e724d026-9c61-8a35-7aea-b10ccb1f7d92\",\"workflowId\":\"68fbb6f8-9a0b-27be-ddf0-6e05365efdc5\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/workflow/addWorkflowBlock/").content(model)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        //JEResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), JEResponse.class);
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void addEmailBlockTest() throws Exception {
        String model = "{\"id\":\"b-a831-1b25-48ec-57b9\",\"type\":\"mailservicetask\",\"attributes\":{\"name\":\"Email Task\",\"description\":\"\",\"lstRecieverAddress\":[\"David@email.com\",\"Mark@gmail.com\"],\"lstAttachementPaths\":[],\"lstBCCs\":[\"David@email.com\",\"Mark@gmail.com\"],\"lstCCs\":[\"David@email.com\",\"Mark@gmail.com\"]},\"projectId\":\"e724d026-9c61-8a35-7aea-b10ccb1f7d92\",\"workflowId\":\"68fbb6f8-9a0b-27be-ddf0-6e05365efdc5\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/workflow/addWorkflowBlock/").content(model)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        //JEResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), JEResponse.class);
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void addSubProcessBlockTest() throws Exception {
        String model = "{\"id\":\"b-a31f-d3c5-23f7-4924\",\"type\":\"callworkflowtask\",\"attributes\":" +
                "{\"name\":\"Call Workflow\",\"description\":\"\"},\"projectId\":" +
                "\"e724d026-9c61-8a35-7aea-b10ccb1f7d92\",\"workflowId\":\"" +
                "68fbb6f8-9a0b-27be-ddf0-6e05365efdc5\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/workflow/addWorkflowBlock/").content(model)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        //JEResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), JEResponse.class);
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void addBoundaryEventBlockTest() throws Exception {
        String model = "{\"id\":\"b-2915-ff78-02aa-2dd9\",\"type\":\"boundaryevent\",\"attributes\":{\"name\":\"Error Handler\",\"description\":\"\"},\"projectId\":\"e724d026-9c61-8a35-7aea-b10ccb1f7d92\",\"workflowId\":\"68fbb6f8-9a0b-27be-ddf0-6e05365efdc5\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/workflow/addWorkflowBlock/").content(model)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        //JEResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), JEResponse.class);
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void addExclusiveGatewayBlockTest() throws Exception {
        String model = "{\"id\":\"b-8624-0abd-12b1-bcaf\",\"type\":\"exclusivegateway\",\"attributes\":{\"name\":\"Exclusive\",\"description\":\"\"},\"projectId\":\"e724d026-9c61-8a35-7aea-b10ccb1f7d92\",\"workflowId\":\"68fbb6f8-9a0b-27be-ddf0-6e05365efdc5\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/workflow/addWorkflowBlock/").content(model)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        //JEResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), JEResponse.class);
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void addInclusiveGatewayBlockTest() throws Exception {
        String model = "{\"id\":\"b-f398-cf7c-08f0-2f52\",\"type\":\"inclusivegateway\",\"attributes\":{\"name\":\"Inclusive\",\"description\":\"\"},\"projectId\":\"e724d026-9c61-8a35-7aea-b10ccb1f7d92\",\"workflowId\":\"68fbb6f8-9a0b-27be-ddf0-6e05365efdc5\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/workflow/addWorkflowBlock/").content(model)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        //JEResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), JEResponse.class);
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void addParallelGatewayBlockTest() throws Exception {
        String model = "{\"id\":\"b-b960-d26b-ab4e-3fa1\",\"type\":\"parallelgateway\",\"attributes\":{\"name\":\"Parallel\",\"description\":\"\"},\"projectId\":\"e724d026-9c61-8a35-7aea-b10ccb1f7d92\",\"workflowId\":\"68fbb6f8-9a0b-27be-ddf0-6e05365efdc5\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/workflow/addWorkflowBlock/").content(model)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        //JEResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), JEResponse.class);
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void addEventBasedGatewayBlockTest() throws Exception {
        String model = "{\"id\":\"b-0fe4-9060-c765-7847\",\"type\":\"eventgateway\",\"attributes\":{\"name\":\"Events\",\"description\":\"\"},\"projectId\":\"e724d026-9c61-8a35-7aea-b10ccb1f7d92\",\"workflowId\":\"68fbb6f8-9a0b-27be-ddf0-6e05365efdc5\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/workflow/addWorkflowBlock/").content(model)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        //JEResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), JEResponse.class);
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void addSignalEventBlockTest() throws Exception {
        String model = "{\"id\":\"b-1c37-a9b7-03dd-50eb\",\"type\":\"signalintermediatecatcheventType\",\"attributes\":{\"name\":\"Accept/Trigger Event\",\"description\":\"\"},\"projectId\":\"e724d026-9c61-8a35-7aea-b10ccb1f7d92\",\"workflowId\":\"68fbb6f8-9a0b-27be-ddf0-6e05365efdc5\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/workflow/addWorkflowBlock/").content(model)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        //JEResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), JEResponse.class);
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void addTimerEventBlockTest() throws Exception {
        String model = "{\"id\":\"b-8238-af9d-8fe6-a6ba\",\"type\":\"cycletimerevent\",\"attributes\":{\"name\":\"Delay Timer\",\"description\":\"\",\"enddate\":\"2022-03-01T15:00:39.965Z\",\"occurrences\":30},\"projectId\":\"e724d026-9c61-8a35-7aea-b10ccb1f7d92\",\"workflowId\":\"68fbb6f8-9a0b-27be-ddf0-6e05365efdc5\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/workflow/addWorkflowBlock/").content(model)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        //JEResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), JEResponse.class);
        assertEquals(200, result.getResponse().getStatus());
    }
}
