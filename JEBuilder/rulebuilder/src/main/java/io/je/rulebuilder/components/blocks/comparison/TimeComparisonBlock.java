package io.je.rulebuilder.components.blocks.comparison;

import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.exceptions.AddRuleBlockException;
import utils.date.DateUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class TimeComparisonBlock extends ComparisonBlock {


    private TimeComparisonBlock() {
    }


    public TimeComparisonBlock(BlockModel blockModel) throws AddRuleBlockException {
        super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getBlockName(),
                blockModel.getDescription(),
                blockModel.getTimePersistenceValue(), blockModel.getTimePersistenceUnit(), blockModel.getInputBlocksIds(), blockModel.getOutputBlocksIds());

        if (blockModel.getBlockConfiguration() != null) {
            LocalDateTime date = LocalDateTime.parse((String) blockModel.getBlockConfiguration()
                    .get(AttributesMapping.VALUE), DateTimeFormatter.ISO_DATE_TIME);

            threshold = "\"" + DateUtils.formatDate(date, ConfigurationConstants.DROOLS_DATE_FORMAT) + "\"";
        }

        operator = getOperatorByOperationId(blockModel.getOperationId());

    }

}
