package io.je;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.project.controllers.WorkflowController;
import io.je.utilities.models.WorkflowBlockModel;
import io.je.utilities.models.WorkflowModel;
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

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UpdateWorkflowBlocksTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @InjectMocks
    WorkflowController workflowController;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void updateWorkflowTest() throws Exception {
        WorkflowModel workflowModel = new WorkflowModel();
        workflowModel.setProjectName("test");
        workflowModel.setProjectId("e724d026-9c61-8a35-7aea-b10ccb1f7d92");
        workflowModel.setId("68fbb6f8-9a0b-27be-ddf0-6e05365efdc5");
        workflowModel.setPath(null);
        workflowModel.setName("sddsqsd");
        workflowModel.setDescription("desssss");
        workflowModel.setTriggeredByEvent(false);
        workflowModel.setOnProjectBoot(true);
        workflowModel.setEndBlockEventId(null);
        workflowModel.setTriggerMessage(null);
        workflowModel.setEvents(null);
        workflowModel.setTasks(null);
        workflowModel.setCreatedBy("administrator");
        workflowModel.setModifiedBy("administrator");
        workflowModel.setStatus(null);
        workflowModel.setModifiedAt(null);
        workflowModel.setCreatedAt(null);
        workflowModel.setFrontConfig(null);
        workflowModel.setEnabled(true);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.
                patch("/workflow/updateWorkflow/e724d026-9c61-8a35-7aea-b10ccb1f7d92/68fbb6f8-9a0b-27be-ddf0-6e05365efdc5").
                content(objectMapper.writeValueAsString(workflowModel))
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        //JEResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), JEResponse.class);
        assertEquals(result.getResponse().getStatus(), 200);
    }

    @Test
    public void updateStartWorkflowBlockTest() throws Exception {
        WorkflowBlockModel blockModel = new WorkflowBlockModel();
        blockModel.setWorkflowId("68fbb6f8-9a0b-27be-ddf0-6e05365efdc5");
        blockModel.setId("b-075c-cbaa-1658-9ccf");
        blockModel.setProjectId("e724d026-9c61-8a35-7aea-b10ccb1f7d92");
        blockModel.setType("start");
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("name", "Start");
        attributes.put("eventId", "eventOne");
        attributes.put("description", "nodes");
        blockModel.setAttributes(attributes);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.
                patch("/workflow/updateWorkflowBlock").
                content(objectMapper.writeValueAsString(blockModel))
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getStatus(), 200);
    }

    @Test
    public void updateEndWorkflowBlockTest() throws Exception {
        WorkflowBlockModel blockModel = new WorkflowBlockModel();
        blockModel.setWorkflowId("68fbb6f8-9a0b-27be-ddf0-6e05365efdc5");
        blockModel.setId("b-bb25-d314-4a39-a8a4");
        blockModel.setProjectId("e724d026-9c61-8a35-7aea-b10ccb1f7d92");
        blockModel.setType("end");
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("name", "Enddsf");
        attributes.put("eventId", "eventTwo");
        attributes.put("description", "nodes");
        blockModel.setAttributes(attributes);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.
                patch("/workflow/updateWorkflowBlock").
                content(objectMapper.writeValueAsString(blockModel))
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getStatus(), 200);
    }

    @Test
    public void updateInformBlockTest() throws Exception {
        WorkflowBlockModel blockModel = new WorkflowBlockModel();
        blockModel.setWorkflowId("68fbb6f8-9a0b-27be-ddf0-6e05365efdc5");
        blockModel.setId("b-c9c2-45c7-effc-2c56");
        blockModel.setProjectId("e724d026-9c61-8a35-7aea-b10ccb1f7d92");
        blockModel.setType("inform");
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("name", "informBlock");
        attributes.put("message", "testmessage");
        attributes.put("description", "");
        blockModel.setAttributes(attributes);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.
                patch("/workflow/updateWorkflowBlock").
                content(objectMapper.writeValueAsString(blockModel))
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getStatus(), 200);
    }

    @Test
    public void updateDBBlockTest() throws Exception {
        WorkflowBlockModel blockModel = new WorkflowBlockModel();
        blockModel.setWorkflowId("68fbb6f8-9a0b-27be-ddf0-6e05365efdc5");
        blockModel.setId("b-d0b5-5c3f-6399-6ba3");
        blockModel.setProjectId("e724d026-9c61-8a35-7aea-b10ccb1f7d92");
        blockModel.setType("dbreadservicetask");
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("name", "dbread");
        attributes.put("request", "testRequest");
        attributes.put("description", "");
        attributes.put("databaseId", "testId");
        blockModel.setAttributes(attributes);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.
                patch("/workflow/updateWorkflowBlock").
                content(objectMapper.writeValueAsString(blockModel))
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getStatus(), 200);
    }

    @Test
    public void updateErrorEventBlockTest() throws Exception {
        WorkflowBlockModel blockModel = new WorkflowBlockModel();
        blockModel.setWorkflowId("68fbb6f8-9a0b-27be-ddf0-6e05365efdc5");
        blockModel.setId("b-2915-ff78-02aa-2dd9");
        blockModel.setProjectId("e724d026-9c61-8a35-7aea-b10ccb1f7d92");
        blockModel.setType("boundaryevent");
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("name", "ErrorHandler");
        attributes.put("description", "");
        blockModel.setAttributes(attributes);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.
                patch("/workflow/updateWorkflowBlock").
                content(objectMapper.writeValueAsString(blockModel))
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getStatus(), 200);
    }

    @Test
    public void updateWebTaskApiBlockTest() throws Exception {
        WorkflowBlockModel blockModel = new WorkflowBlockModel();
        blockModel.setWorkflowId("68fbb6f8-9a0b-27be-ddf0-6e05365efdc5");
        blockModel.setId("b-593e-ee8f-09e3-e82b");
        blockModel.setProjectId("e724d026-9c61-8a35-7aea-b10ccb1f7d92");
        blockModel.setType("webtask");
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("name", "webtask");
        attributes.put("description", "");
        attributes.put("authscheme", "API_KEY");
        attributes.put("body", "{\\n  \\\"param1\\\":\\\"value1\\\"\\n}");
        attributes.put("method", "POST");
        HashMap<String, String> authentication = new HashMap<>();
        authentication.put("key", "somekey");
        authentication.put("value", "keyvalue");
        attributes.put("authentication", authentication);
        HashMap<String, String> output = new HashMap<>();
        authentication.put("outputVar", "outputValue");
        attributes.put("outputs", output);
        blockModel.setAttributes(attributes);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.
                patch("/workflow/updateWorkflowBlock").
                content(objectMapper.writeValueAsString(blockModel))
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getStatus(), 200);
    }

    @Test
    public void updateScriptTaskApiBlockTest() throws Exception {
        WorkflowBlockModel blockModel = new WorkflowBlockModel();
        blockModel.setWorkflowId("68fbb6f8-9a0b-27be-ddf0-6e05365efdc5");
        blockModel.setId("b-2036-7b2d-44bc-09cd");
        blockModel.setProjectId("e724d026-9c61-8a35-7aea-b10ccb1f7d92");
        blockModel.setType("scripttask");
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("name", "Script");
        attributes.put("description", "");
        attributes.put("script", "System.out.println(\"e\");");
        attributes.put("timeout", 600);
        ArrayList<String> imports = new ArrayList<>();
        imports.add("org.apache.commons.lang3.StringUtils");
        attributes.put("imports", imports);
        blockModel.setAttributes(attributes);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.
                patch("/workflow/updateWorkflowBlock").
                content(objectMapper.writeValueAsString(blockModel))
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getStatus(), 200);
    }

    @Test
    public void updateSubProcessTaskApiBlockTest() throws Exception {
        WorkflowBlockModel blockModel = new WorkflowBlockModel();
        blockModel.setWorkflowId("68fbb6f8-9a0b-27be-ddf0-6e05365efdc5");
        blockModel.setId("b-a31f-d3c5-23f7-4924");
        blockModel.setProjectId("e724d026-9c61-8a35-7aea-b10ccb1f7d92");
        blockModel.setType("callworkflowtask");
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("name", "subpocesstest");
        attributes.put("description", "");
        attributes.put("subworkflowId", "75ec0cca-f136-e983-1a37-7c420caab783");
        blockModel.setAttributes(attributes);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.
                patch("/workflow/updateWorkflowBlock").
                content(objectMapper.writeValueAsString(blockModel))
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getStatus(), 200);
    }

    @Test
    public void updateScheduleWorkflowBlockTest() throws Exception {
        WorkflowBlockModel blockModel = new WorkflowBlockModel();
        blockModel.setWorkflowId("68fbb6f8-9a0b-27be-ddf0-6e05365efdc5");
        blockModel.setId("b-600d-3558-2f26-6df3");
        blockModel.setProjectId("e724d026-9c61-8a35-7aea-b10ccb1f7d92");
        blockModel.setType("start");
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("name", "Start1");
        attributes.put("eventType", "cycletimerevent");
        attributes.put("description", "nodes");
        attributes.put("occurrences", 30);
        attributes.put("timecycle", "30s");
        attributes.put("enddate", "2022-03-01T15:00:39.953Z");
        blockModel.setAttributes(attributes);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.
                patch("/workflow/updateWorkflowBlock").
                content(objectMapper.writeValueAsString(blockModel))
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getStatus(), 200);

        attributes.put("eventType", "durationtimerevent");
        blockModel.setAttributes(attributes);
        requestBuilder = MockMvcRequestBuilders.
                patch("/workflow/updateWorkflowBlock").
                content(objectMapper.writeValueAsString(blockModel))
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getStatus(), 200);

        attributes.put("eventType", "datetimerevent");
        blockModel.setAttributes(attributes);
        requestBuilder = MockMvcRequestBuilders.
                patch("/workflow/updateWorkflowBlock").
                content(objectMapper.writeValueAsString(blockModel))
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getStatus(), 200);
    }

    @Test
    public void updateExclusiveGatewayBlockTest() throws Exception {
        WorkflowBlockModel blockModel = new WorkflowBlockModel();
        blockModel.setWorkflowId("68fbb6f8-9a0b-27be-ddf0-6e05365efdc5");
        blockModel.setId("b-8624-0abd-12b1-bcaf");
        blockModel.setProjectId("e724d026-9c61-8a35-7aea-b10ccb1f7d92");
        blockModel.setType("exclusivegateway");
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("name", "Exclusive");
        attributes.put("description", "");
        blockModel.setAttributes(attributes);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.
                patch("/workflow/updateWorkflowBlock").
                content(objectMapper.writeValueAsString(blockModel))
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getStatus(), 200);
    }

    @Test
    public void updateParallelGatewayBlockTest() throws Exception {
        WorkflowBlockModel blockModel = new WorkflowBlockModel();
        blockModel.setWorkflowId("68fbb6f8-9a0b-27be-ddf0-6e05365efdc5");
        blockModel.setId("b-b960-d26b-ab4e-3fa1");
        blockModel.setProjectId("e724d026-9c61-8a35-7aea-b10ccb1f7d92");
        blockModel.setType("parallelgateway");
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("name", "parallel");
        attributes.put("description", "");
        blockModel.setAttributes(attributes);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.
                patch("/workflow/updateWorkflowBlock").
                content(objectMapper.writeValueAsString(blockModel))
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getStatus(), 200);
    }

    @Test
    public void updateEventGatewayBlockTest() throws Exception {
        WorkflowBlockModel blockModel = new WorkflowBlockModel();
        blockModel.setWorkflowId("68fbb6f8-9a0b-27be-ddf0-6e05365efdc5");
        blockModel.setId("b-0fe4-9060-c765-7847");
        blockModel.setProjectId("e724d026-9c61-8a35-7aea-b10ccb1f7d92");
        blockModel.setType("eventgateway");
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("name", "EventGateway");
        attributes.put("description", "");
        blockModel.setAttributes(attributes);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.
                patch("/workflow/updateWorkflowBlock").
                content(objectMapper.writeValueAsString(blockModel))
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getStatus(), 200);
    }

    @Test
    public void updateInclusiveGatewayBlockTest() throws Exception {
        WorkflowBlockModel blockModel = new WorkflowBlockModel();
        blockModel.setWorkflowId("68fbb6f8-9a0b-27be-ddf0-6e05365efdc5");
        blockModel.setId("b-f398-cf7c-08f0-2f52");
        blockModel.setProjectId("e724d026-9c61-8a35-7aea-b10ccb1f7d92");
        blockModel.setType("inclusivegateway");
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("name", "Inclusive");
        attributes.put("description", "");
        blockModel.setAttributes(attributes);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.
                patch("/workflow/updateWorkflowBlock").
                content(objectMapper.writeValueAsString(blockModel))
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getStatus(), 200);
    }

    @Test
    public void updateSignalEventBlockTest() throws Exception {
        WorkflowBlockModel blockModel = new WorkflowBlockModel();
        blockModel.setWorkflowId("68fbb6f8-9a0b-27be-ddf0-6e05365efdc5");
        blockModel.setId("b-1c37-a9b7-03dd-50eb");
        blockModel.setProjectId("e724d026-9c61-8a35-7aea-b10ccb1f7d92");
        blockModel.setType("signalThrowEventType");
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("name", "AccepTriggerEvent");
        attributes.put("description", "");
        attributes.put("eventId", "eventOne");
        blockModel.setAttributes(attributes);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.
                patch("/workflow/updateWorkflowBlock").
                content(objectMapper.writeValueAsString(blockModel))
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getStatus(), 200);

        blockModel.setType("signalThrowEventType");
        result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        assertEquals(result.getResponse().getStatus(), 200);
    }


    @Test
    public void addSequenceFlowTest() throws Exception {
        String model = "{\"id\":\"b-075c-cbaa-1658-9ccfb-bb25-d314-4a39-a8a4\",\"" +
                "type\":\"sequenceflow\",\"attributes\":{\"sourceRef\":\"" +
                "b-c9c2-45c7-effc-2c56\",\"targetRef\":\"b-8624-0abd-12b1-bcaf\"" +
                "},\"projectId\":\"e724d026-9c61-8a35-7aea-b10ccb1f7d92\",\"workflowId\"" +
                ":\"68fbb6f8-9a0b-27be-ddf0-6e05365efdc5\"}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/workflow/addWorkflowBlock/").content(model)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType("application/json");
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        //JEResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), JEResponse.class);
        assertEquals(result.getResponse().getStatus(), 200);
    }
}
