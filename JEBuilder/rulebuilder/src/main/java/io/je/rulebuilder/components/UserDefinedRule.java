package io.je.rulebuilder.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.ComparisonBlock;
import io.je.rulebuilder.components.blocks.ConditionBlock;
import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.components.blocks.LogicBlock;
import io.je.rulebuilder.components.blocks.arithmetic.BiasBlock;
import io.je.rulebuilder.components.blocks.arithmetic.DivideBlock;
import io.je.rulebuilder.components.blocks.arithmetic.FunctionBlock;
import io.je.rulebuilder.components.blocks.arithmetic.GainBlock;
import io.je.rulebuilder.components.blocks.arithmetic.MultiplyBlock;
import io.je.rulebuilder.components.blocks.arithmetic.PowerBlock;
import io.je.rulebuilder.components.blocks.arithmetic.SubtractBlock;
import io.je.rulebuilder.components.blocks.arithmetic.SumBlock;
import io.je.rulebuilder.components.blocks.arithmetic.UnitConversionBlock;
import io.je.rulebuilder.components.blocks.comparison.GreaterThanBlock;
import io.je.rulebuilder.components.blocks.execution.LogBlock;
import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;
import io.je.rulebuilder.components.blocks.logic.AndBlock;
import io.je.rulebuilder.components.blocks.logic.JoinBlock;
import io.je.rulebuilder.components.blocks.logic.NotBlock;
import io.je.rulebuilder.components.blocks.logic.OrBlock;
import io.je.rulebuilder.components.blocks.logic.XorBlock;
import io.je.rulebuilder.config.LogConstants;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.RuleModel;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.exceptions.RuleNotAddedException;
import io.je.utilities.logger.JELogger;

/*
 * Rules defined by the user.
 * One UserDefinedRule can be equivalents to multiple JobEngine rules ( or drls)
 * Each Job engine rule is defined by a root block ( a logic or comparison block that precedes and execution sequence)
 */

public class UserDefinedRule {

	/*
	 * project id
	 */
	private String projectId;

	/*
	 * rule id
	 */
	private String ruleId;

	/*
	 * rule priority
	 */
	private String salience;

	/*
	 * rule parameter that indicates whether a rule is enabled or disbaled
	 */
	private boolean enabled;

	/*
	 * rule parameter that indicates when the rule should be activated
	 */
	private String dateEffective;

	/*
	 * rule parameter that indicates when the rule expires
	 */
	private String dateExpires;

	/*
	 * cron expression that defines the rule's firing schedule
	 */
	private String timer;

	/*
	 * Map of all the blocks that define this rule
	 */
	Map<String, Block> blocks = new HashMap<>();

	/*
	 * Constructor : generates a user defined rule from a rule model
	 */
	public UserDefinedRule(RuleModel ruleModel) throws RuleNotAddedException {
		super();

		// exception when the project id is empty;
		if (ruleModel.getProjectId() == null) {
			JELogger.error(getClass(), LogConstants.ProjectIdentifierIsEmpty);
			throw new RuleNotAddedException("400", LogConstants.ProjectIdentifierIsEmpty);

		}

		// exception if rule id is null;
		if (ruleModel.getRuleId() == null) {
			JELogger.error(getClass(), LogConstants.RuleIdentifierIsEmpty);
			throw new RuleNotAddedException("400", LogConstants.RuleIdentifierIsEmpty);

		}
		this.projectId = ruleModel.getProjectId();
		this.ruleId = ruleModel.getRuleId();
		this.salience = ruleModel.getSalience();
		this.enabled = ruleModel.isEnabled();
		this.dateEffective = ruleModel.getDateEffective();
		this.dateExpires = ruleModel.getDateExpires();
		this.timer = ruleModel.getTimer();
	}

	/*
	 * this method builds the user defined rule and generates unit rules (JERules)
	 * from it
	 */
	public List<JERule> build() throws RuleBuildFailedException {

		// number of execution blocks
		int executionBlockCounter = 0;

		// set of root blocks : blocks that precede an execution block.
		// Each root block defines a job engine rule
		Set<ConditionBlock> roots = new HashSet<>();

		// get root blocks
		for (Block ruleBlock : blocks.values()) {
			if (ruleBlock instanceof ExecutionBlock) {
				executionBlockCounter++;
				for (String rootBlockId : ruleBlock.getInputBlocks()) {
					roots.add((ConditionBlock) blocks.get(rootBlockId));
				}

			}
		}

		// if this rule has no execution block, then it is not valid.
		if (executionBlockCounter == 0) {
			throw new RuleBuildFailedException("400", LogConstants.NoExecutionBlock);
		}

		// generate JERules
		List<JERule> rules = new ArrayList<>();

		int jeRuleCounter = 0;
		for (ConditionBlock root : roots) {
			JERule rule = generateJERule(root);
			rule.setJobEngineElementID(ruleId + jeRuleCounter);
			rules.add(rule);
		}
		return rules;
	}

	/*
	 * generate a job engine rule from a root block
	 */
	public JERule generateJERule(ConditionBlock block) {
		JERule rule = new JERule(ruleId, projectId);
		rule.setSalience(salience);
		rule.setDateEffective(dateEffective);
		rule.setDateExpires(dateExpires);
		rule.setEnabled(enabled);
		rule.setTimer(timer);
		rule.setCondition(buildCondition(block));
		rule.setConsequences(getConsequence(block));
		return rule;

	}

	/*
	 * retrieve list of consequences relative to a JERule
	 */
	private List<Consequence> getConsequence(ConditionBlock block) {
		List<Consequence> consequences = new ArrayList<>();
		for (String blockId : block.getOutputBlocks()) {
			if (blocks.get(blockId) instanceof ExecutionBlock) {
				consequences.add(new Consequence((ExecutionBlock) blocks.get(blockId)));
			}
		}
		return consequences;

	}

	/*
	 * generates a condition tree with the input block as a root
	 */
	private ConditionBlockNode buildCondition(ConditionBlock block) {
		ConditionBlockNode conditionBlockNode = new ConditionBlockNode(block);

		if (block.getInputBlocks() == null || block.getInputBlocks().isEmpty()) {
			return conditionBlockNode;
		} else {
			for (String inputBlockId : block.getInputBlocks()) {
				ConditionBlock inputBlock = (ConditionBlock) blocks.get(inputBlockId);
				if (inputBlock != null) {
					conditionBlockNode.addChild(buildCondition(inputBlock));

				}
			}
		}
		return conditionBlockNode;

	}

	/*
	 * add a block to this user defined rule
	 */
	public void addBlock(BlockModel blockModel) {
		if (blockModel == null || blockModel.getOperationId() == 0 || blockModel.getBlockId() == null
				|| blockModel.getBlockId().isEmpty()) {
			// throw exception, can't add block
			return;

		}

		Block block = generateBlock(blockModel);
		JELogger.info(getClass(), block.toString());

		blocks.put(blockModel.getBlockId(), block);

	}

	/*
	 * returns a new block created from a block model
	 */
	private Block generateBlock(BlockModel blockModel) {
		switch (blockModel.getOperationId()) {
		/*
		 * Arithmetic blocks
		 */

		// sum
		case 1001:
			return new SumBlock(blockModel);
		// Subtract
		case 1002:
			return new SubtractBlock(blockModel);
		// Multiply
		case 1003:
			return new MultiplyBlock(blockModel);
		// Divide
		case 1004:
			return new DivideBlock(blockModel);
		// Factorial
		case 1005:
			return new FunctionBlock(blockModel, 1005);
		// Square
		case 1006:
			return new FunctionBlock(blockModel, 1006);
		// SquareRoot
		case 1007:
			return new FunctionBlock(blockModel, 1007);
		// Power
		case 1008:
			return new PowerBlock(blockModel);
		// change sign
		case 1009:
			return new FunctionBlock(blockModel, 1009);
		case 1010:
			return new BiasBlock(blockModel);
		case 1011:
			return new GainBlock(blockModel);
		case 1012:
			return new FunctionBlock(blockModel, 1012);
		case 1013:
			return new FunctionBlock(blockModel, 1013);
		case 1014:
			return new FunctionBlock(blockModel, 1014);
		case 1015:
			return new FunctionBlock(blockModel, 1015);
		case 1016:
			return new FunctionBlock(blockModel, 1016);
		case 1017:
			return new FunctionBlock(blockModel, 1017);
		case 1018:
			return new FunctionBlock(blockModel, 1018);
		case 1019:
			return new FunctionBlock(blockModel, 1019);
		case 1020:
			return new FunctionBlock(blockModel, 1020);
		case 1021:
			return new FunctionBlock(blockModel, 1021);
		case 1022:
			return new FunctionBlock(blockModel, 1022);
		case 1023:
			return new FunctionBlock(blockModel, 1023);
		case 1024:
			return new FunctionBlock(blockModel, 1024);
		case 1025:
			return new FunctionBlock(blockModel, 1025);
		case 1026:
			break;
		case 1027:
			return new UnitConversionBlock(blockModel);

		/*
		 * Comparison blocks
		 */
		case 2001:
			break;
		case 2002:
			break;
		case 2003:
			return new GreaterThanBlock(blockModel);
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
			return new AttributeGetterBlock(blockModel);
		case 4003:
			break;
		/*
		 * Execution blocks
		 */
		case 5001:
			return new LogBlock(blockModel);

		// no operation with such id
		default:
			return null;
		}
		return null;

	}

	/*
	 * Getters and setters
	 */

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

	public String getTimer() {
		return timer;
	}

	public void setTimer(String timer) {
		this.timer = timer;
	}

	@Override
	public String toString() {
		return "UserDefinedRule [projectId=" + projectId + ", ruleId=" + ruleId + ", salience=" + salience
				+ ", enabled=" + enabled + ", dateEffective=" + dateEffective + ", dateExpires=" + dateExpires
				+ ", timer=" + timer + ", blocks=" + blocks + "]";
	}

}
