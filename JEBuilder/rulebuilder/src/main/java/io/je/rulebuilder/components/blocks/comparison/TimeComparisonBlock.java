package io.je.rulebuilder.components.blocks.comparison;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;
import io.siothconfig.SIOTHConfigUtility;
import utils.date.DateUtils;


public class TimeComparisonBlock extends ComparisonBlock {
	
	
	
	private TimeComparisonBlock() {
	}



	public TimeComparisonBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(),blockModel.getBlockName(),
				blockModel.getDescription() ,
				blockModel.getTimePersistenceValue(),blockModel.getTimePersistenceUnit(),blockModel.getInputBlocksIds(),blockModel.getOutputBlocksIds());
		if(blockModel.getBlockConfiguration()!=null )
		{
			LocalDateTime date = LocalDateTime.parse((String)blockModel.getBlockConfiguration().get(AttributesMapping.VALUE), DateTimeFormatter.ISO_DATE_TIME);

			threshold = "\""+ DateUtils.formatDate(date, SIOTHConfigUtility.getSiothConfig().getDateFormat())+"\"";
		}
		
		operator = getOperatorByOperationId(blockModel.getOperationId());
		
	}

}
