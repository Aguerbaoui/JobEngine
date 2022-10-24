package io.je.rulebuilder.components.blocks.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.log.JELogger;
import lombok.Getter;
import lombok.Setter;
import utils.log.LoggerUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.je.rulebuilder.config.AttributesMapping.*;

@Getter
@Setter
public class DFAction extends ExecutionBlock {
    private List<DFActionPart> actions = new ArrayList<DFActionPart>();

    public DFAction(BlockModel blockModel) {
        super(blockModel);
        try {
            List<DFActionPart> listOfActions = (List<DFActionPart>)blockModel.getBlockConfiguration().get(ACTIONS);
            if (listOfActions != null) {
                for (int i = 0; i < listOfActions.toArray().length; i++) {
                    DFActionPart temp = new DFActionPart();
                    temp.startStop = (String)((HashMap<DFActionPart, String>)listOfActions.toArray()[i]).get(ACTION);
                    temp.name = (String)((HashMap<DFActionPart, String>)listOfActions.toArray()[i]).get(NAME);
                    temp.connectors = (List<String>)((HashMap<DFActionPart, List<String>>)listOfActions.toArray()[i]).get(CONNECTORS);
                    temp.connectorsID = (List<String>)((HashMap<DFActionPart, List<String>>)listOfActions.toArray()[i]).get(CONNECTORSID);
                    temp.dataFlowID = (String)((HashMap<DFActionPart, String>)listOfActions.toArray()[i]).get(DATAFLOWID);
                    temp.actionID = (String)((HashMap<DFActionPart, String>)listOfActions.toArray()[i]).get(ACTIONID);
                    this.actions.add(temp);
                }
            }
        } catch (Exception e) {
            isProperlyConfigured = false;
            JELogger.logException(e);
        }
    }

    public DFAction() {
        super();
    }

    @Override
    public String getExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put(ACTIONS, getActions());

        String json = null;
        try {
            ObjectWriter ow = new ObjectMapper().writer()
                    .withDefaultPrettyPrinter();
            json = ow.writeValueAsString(attributes);
        } catch (JsonProcessingException e) {
            LoggerUtils.logException(e);
            throw new RuntimeException(e);
        }
        expression.append("Executioner.TakeDataFlowAction( " + "\"")
                .append(this.jobEngineProjectID)
                .append("\",")
                .append("\"")
                .append(this.ruleId)
                .append("\",")
                .append("\"")
                .append(this.blockName)
                .append("\",")
                .append(json)
                .append(");\r\n");;
        expression.append("\n");
        return expression.toString();
    }
}

class DFActionPart {
    public String startStop;
    public String name;
    public String dataFlowID;
    public List<String> connectors;
    public List<String> connectorsID;
    public String actionID;
}