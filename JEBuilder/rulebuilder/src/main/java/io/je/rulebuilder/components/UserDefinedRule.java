package io.je.rulebuilder.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.ComparisonBlock;
import io.je.rulebuilder.components.blocks.ConditionBlock;
import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.components.blocks.LogicBlock;
import io.je.rulebuilder.components.blocks.arithmetic.SumBlock;
import io.je.rulebuilder.components.blocks.logic.AndBlock;
import io.je.rulebuilder.components.blocks.logic.JoinBlock;
import io.je.rulebuilder.components.blocks.logic.NotBlock;
import io.je.rulebuilder.components.blocks.logic.OrBlock;
import io.je.rulebuilder.components.blocks.logic.XorBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.RuleModel;

/*
 * Rules defined by the user.
 * One UserDefinedRule can be equivalents to multiple JobEngine rules ( or drls)
 */

public class UserDefinedRule {

	String projectId;
	String ruleId;
	String salience;
	boolean enabled;
	String dateEffective;
	String dateExpires;
	String duration;
	String timer;
	String calendar;
	Map<String, Block> blocks = new HashMap<>();

	public UserDefinedRule(RuleModel ruleModel) {
		super();

		// TODO: throw exception if ids are null
		this.projectId = ruleModel.getProjectId();
		this.ruleId = ruleModel.getRuleId();
		this.salience = ruleModel.getSalience();
		this.enabled = ruleModel.isEnabled();
		this.dateEffective = ruleModel.getDateEffective();
		this.dateExpires = ruleModel.getDateExpires();
		this.duration = ruleModel.getDuration();
		this.timer = ruleModel.getTimer();
		this.calendar = ruleModel.getCalendar();
	}

	
	/*
	 * build user defined rule and generate job engine rules from it
	 */
	public List<JERule> build() {

		// get root blocks : blocks that precede an execution block.
		// Each root block defines a job engine rule
		List<ConditionBlock> roots = new ArrayList<>();
		for (Block block : blocks.values()) {
			if (block instanceof LogicBlock || block instanceof ComparisonBlock) {
				for (String blockid : block.getOutputBlocks()) {
					// if one of its output blocks is an execution block then it's a root block
					if (blocks.get(blockid) instanceof ExecutionBlock) {
						roots.add((ConditionBlock) block);
						break;
					}
				}
			}
		}

		//generate JERules
		List<JERule> rules = new ArrayList<>();
		for(ConditionBlock root:roots)
		{
			rules.add(generateJERule(root));
		}
		return rules;
	}

	public JERule generateJERule(ConditionBlock block) {
		JERule rule = new JERule(ruleId, projectId);
		rule.setSalience(salience);
		rule.setDateEffective(dateEffective);
		rule.setDateExpires(dateExpires);
		rule.setDuration(duration);
		rule.setEnabled(enabled);
		rule.setTimer(timer);
		rule.setCalendar(calendar);
		rule.setCondition(buildCondition(block));
		rule.setConsequences(getConsequence(block));
		return rule;
		

	}

	private List<Consequence> getConsequence(ConditionBlock block)
	{
		List<Consequence> consequences = new ArrayList<>();
		for (String blockId : block.getOutputBlocks()) {
			if(blocks.get(blockId) instanceof ExecutionBlock)
			{
				consequences.add( new Consequence( (ExecutionBlock) blocks.get(blockId) ));
			}
		}
		return consequences;
		
	}
	
	private Condition buildCondition(ConditionBlock block) {
		Condition condition = new Condition(block);

		if (block.getInputBlocks().isEmpty()) {
			return condition;
		}

		else {
			for (String inputBlockId : block.getInputBlocks()) {
				ConditionBlock inputBlock = (ConditionBlock) blocks.get(inputBlockId);
				if (inputBlock != null) {
					condition.addChild(buildCondition(inputBlock));

				}
			}
		}
		return condition;

	}

	public void addBlock(BlockModel blockModel) {
		if (blockModel == null || blockModel.getOperationId() == 0 || blockModel.getBlockId() == null
				|| blockModel.getBlockId().isEmpty()) {
			// throw exception, can't add block
			return;

		}

		Block block = generateBlock(blockModel);
		blocks.put(blockModel.getBlockId(), block);

	}

	private Block generateBlock(BlockModel blockModel) {
		switch (blockModel.getOperationId()) {
		/*
		 * Arithmetic blocks
		 */

		// sum block
		case 1001:
			return new SumBlock(blockModel);
		case 1002:
			break;
		case 1003:
			break;
		case 1004:
			break;
		case 1005:
			break;
		case 1006:
			break;
		case 1007:
			break;
		case 1008:
			break;
		case 1009:
			break;
		case 1010:
			break;
		case 1011:
			break;
		case 1012:
			break;
		case 1013:
			break;
		case 1014:
			break;
		case 1015:
			break;
		case 1016:
			break;
		case 1017:
			break;
		case 1018:
			break;
		case 1019:
			break;
		case 1020:
			break;
		case 1021:
			break;
		case 1022:
			break;
		case 1023:
			break;
		case 1024:
			break;
		case 1025:
			break;
		case 1026:
			break;

		/*
		 * Comparison blocks
		 */
		case 2001:
			break;
		case 2002:
			break;
		case 2003:
			break;
		case 2004:
			break;
		case 2005:
			break;
		case 2006:
			break;
		case 2007:
			break;
		case 2008:
			break;
		case 2009:
			break;
		case 2010:
			break;
		case 2011:
			break;
		case 2012:
			break;
		case 2013:
			break;
		case 2014:
			break;
		case 2015:
			break;
		/*
		 * Logic blocks
		 */

		// And Block
		case 3001:
			return new AndBlock(blockModel);
		// Or Block
		case 3002:
			return new OrBlock(blockModel);
		// XOR Block
		case 3003:
			return new XorBlock(blockModel);
		// Join Block
		case 3004:
			return new JoinBlock(blockModel);
		// NOT Block
		case 3005:
			return new NotBlock(blockModel);

		case 3:
			break;
		/*
		 * Getter blocks
		 */
		case 4001:
			break;
		case 4002:
			break;
		case 4003:
			break;

		// no operation with such id
		default:
			return null;
		}
		return null;

	}

	public Map<String, Block> getBlocks() {
		return blocks;
	}

	public void setBlocks(Map<String, Block> blocks) {
		this.blocks = blocks;
	}

	public void updateBlock(Block block) {

	}

	public void deletBlock(String blockId) {

	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getSalience() {
		return salience;
	}

	public void setSalience(String salience) {
		this.salience = salience;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getDateEffective() {
		return dateEffective;
	}

	public void setDateEffective(String dateEffective) {
		this.dateEffective = dateEffective;
	}

	public String getDateExpires() {
		return dateExpires;
	}

	public void setDateExpires(String dateExpires) {
		this.dateExpires = dateExpires;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getTimer() {
		return timer;
	}

	public void setTimer(String timer) {
		this.timer = timer;
	}

	public String getCalendar() {
		return calendar;
	}

	public void setCalendar(String calendar) {
		this.calendar = calendar;
	}

}
