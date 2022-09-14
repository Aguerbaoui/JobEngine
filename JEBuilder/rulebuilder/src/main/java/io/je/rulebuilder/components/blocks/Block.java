package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.components.BlockLink;
import io.je.rulebuilder.components.BlockLinkModel;
import io.je.rulebuilder.components.blocks.getter.InstanceGetterBlock;
import io.je.rulebuilder.components.blocks.getter.VariableGetterBlock;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.runtimeobject.JEObject;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/*
 * Job Engine block
 */
@Document(collection = "RuleBlock")
public abstract class Block extends JEObject {


    protected String ruleId;
    protected String blockName; // FIXME block ID needed at this level
    protected String blockDescription;

    protected boolean isProperlyConfigured = true;
    // TODO Set different causes, Externalize messages
    protected String misConfigurationCause = "";

    @Transient
    protected List<BlockLink> inputBlockLinks = new ArrayList<>();

    @Transient
    protected List<BlockLink> outputBlockLinks = new ArrayList<>();


    protected boolean alreadyScripted = false;


    /*
     * to be persisted in mongo
     */
    protected List<BlockLinkModel> inputBlockIds = new ArrayList<>();


    protected List<BlockLinkModel> outputBlockIds = new ArrayList<>();


    public Block(String jobEngineElementID, String jobEngineProjectID, String ruleId, String blockName,
                 String blockDescription, List<BlockLinkModel> inputBlockIds, List<BlockLinkModel> outputBlocksIds) {

        super(jobEngineElementID, jobEngineProjectID, blockName);

        this.ruleId = ruleId;
        this.blockName = blockName;
        this.blockDescription = blockDescription;
        this.isProperlyConfigured = true;
        if (blockName == null) {
            this.isProperlyConfigured = false;
            this.misConfigurationCause = JEMessages.BLOCK_NAME_IS_NULL;
        }
        this.inputBlockIds = inputBlockIds;
        this.outputBlockIds = outputBlocksIds;

    }


    public Block() {

    }

    public abstract String getReference(String optional);


    public void addInputLink(Block block, String connectionName, int order) {

        inputBlockLinks.add(new BlockLink(block, order, connectionName));

    }

    public void addOutputLink(Block block, String connectionName, int order) {

        outputBlockLinks.add(new BlockLink(block, order, connectionName));

    }


    //return drl expression of block
    public abstract String getExpression() throws RuleBuildFailedException;


    public abstract String getAsOperandExpression() throws RuleBuildFailedException;

    /*
     * get name of variable holding he value expressed by input number index: ex: $age, $block1 ...
     */
    public String getRefName(String optional) {
        String var = "";

        if (this instanceof InstanceGetterBlock) {//get attribute var name
            var = ((InstanceGetterBlock) this).getAttributeVariableName(optional);
        } else if (this instanceof VariableGetterBlock) {
            var = ((VariableGetterBlock) this).getAttributeVariableName();
        } else {//get block name as variable
            var = this.getBlockNameAsVariable();
        }
        return var;
    }

    public String getBlockNameAsVariable() {
        return blockName.replaceAll("\\s+", "");
    }

    public String getRuleId() {
        return ruleId;
    }


    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public List<BlockLink> getOutputBlockLinks() {
        return outputBlockLinks;
    }

    public void setOutputBlockLinks(List<BlockLink> outputBlockLinks) {
        this.outputBlockLinks = outputBlockLinks;
    }

    public String getBlockDescription() {
        return blockDescription;
    }

    public void setBlockDescription(String blockDescription) {
        this.blockDescription = blockDescription;
    }

    public List<BlockLinkModel> getInputBlockIds() {
        return inputBlockIds;
    }

    public void setInputBlockIds(List<BlockLinkModel> inputBlockIds) {
        this.inputBlockIds = inputBlockIds;
    }

    public List<BlockLinkModel> getOutputBlockIds() {
        return outputBlockIds;
    }

    public void setOutputBlockIds(List<BlockLinkModel> outputBlockIds) {
        this.outputBlockIds = outputBlockIds;
    }

    //ignore block FIXME is it working
    public void ignoreBlock() {
        for (var inputBlock : inputBlockLinks) {
            inputBlock.getBlock().outputBlockLinks.addAll(outputBlockLinks);
        }

        for (var outputBlock : outputBlockLinks) {
            outputBlock.getBlock().inputBlockLinks.addAll(inputBlockLinks);
        }
    }

    public boolean isProperlyConfigured() {
        return this.isProperlyConfigured;
    }

    public void setProperlyConfigured(boolean isProperlyConfigured) {
        this.isProperlyConfigured = isProperlyConfigured;
    }

    public String getMisConfigurationCause() {
        return misConfigurationCause;
    }

    public void setMisConfigurationCause(String misConfigurationCause) {
        this.misConfigurationCause = misConfigurationCause;
    }

    public boolean isAlreadyScripted() {
        return alreadyScripted;
    }

    public void setAlreadyScripted(boolean alreadyScripted) {
        this.alreadyScripted = alreadyScripted;
    }

    public String getPersistence() {
        if (this instanceof PersistableBlock) {
            PersistableBlock pBlock = (PersistableBlock) this;
            String persistence = pBlock.getPersistenceExpression();
            if (persistence != null) {
                return persistence;
            } else if (pBlock.getInputBlockLinks()
                    .isEmpty()) {
                return null;

            } else {
                for (var b : inputBlockLinks) {
                    if (b.getBlock()
                            .getPersistence() != null) {
                        return b.getBlock()
                                .getPersistence();
                    }
                }
            }

        }
        return null;
    }

    public Block getInputBlockByOrder(int order) {
        var input = inputBlockLinks.stream()
                .filter(x -> x.getOrder() == order)
                .findFirst();
        return input.map(BlockLink::getBlock)
                .orElse(null);
    }

    public String getInputReferenceByOrder(int order) {
        var input = inputBlockLinks.stream()
                .filter(x -> x.getOrder() == order)
                .findFirst();
        return input.map(BlockLink::getReference)
                .orElse(null);
    }

    public List<Block> getInputsByOrder(int order) {
        return inputBlockLinks.stream()
                .filter(x -> x.getOrder() == order)
                .map(BlockLink::getBlock)
                .collect(Collectors.toList());
    }

    public boolean hasPrecedent(Block block) {
        if (inputBlockLinks.isEmpty()) {
            return false;
        } else {
            if (inputBlockLinks.stream()
                    .anyMatch(x -> x.getBlock()
                            .equals(block))) {
                return true;
            }
            for (var b : inputBlockLinks) {
                if (b.getBlock()
                        .hasPrecedent(block)) {
                    return true;
                }
            }
        }

        return false;
    }

    // FIXME : should loop on input block till getters (data sources) : check Bug 5209
    public Set<String> getAllInputBlocksExpressions() throws RuleBuildFailedException {
        Set<String> allInputBlocksExpressions = new HashSet<>();

        for (var blockLink : inputBlockLinks) {
            if (blockLink.getBlock() != null) {
                for (var block : blockLink.getBlock().getInputBlockLinks()) {
                    allInputBlocksExpressions.add(block.getExpression());
                }
            }
        }

        return allInputBlocksExpressions;
    }

    public List<BlockLink> getInputBlockLinks() {
        return inputBlockLinks;
    }

    public void setInputBlockLinks(List<BlockLink> inputBlockLinks) {
        this.inputBlockLinks = inputBlockLinks;
    }

    // TODO finish and use in getAllInputBlocksExpressions
    public Set<Block> getAllGetterBlocksExpressions() {
        Set<Block> getterInputBlocks = new HashSet<>();

        for (var block : inputBlockLinks) {
            if (block.getBlock() instanceof InstanceGetterBlock) {
                getterInputBlocks.add(block.getBlock());
            } else if (block.getBlock() instanceof VariableGetterBlock) {
                getterInputBlocks.add(block.getBlock());
            }
        }

        return getterInputBlocks.stream().sorted((o1, o2) -> o1.getBlockName().compareTo(o2.getBlockName())).collect(Collectors.toSet());
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String formatMessage(String message) {
        String msg = message;
        Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");

        Matcher matcher = pattern.matcher(msg);
        ArrayList<String> wordsToBeReplaced = new ArrayList<String>();
        while (matcher.find()) {
            wordsToBeReplaced.add(matcher.group());
        }
        for (String word : wordsToBeReplaced) {
            String tword = word.replace("${", "");
            String tword2 = tword.replace("}", "");
            msg = msg.replace(word, "\" + " + tword2 + " + \"");
        }
        return msg;
    }

    public String asDouble(String val) {
        return "JEMathUtils.castToDouble(\"" + this.jobEngineProjectID + "\",\"" + this.ruleId + "\",\"" + this.blockName + "\"," + val + " ) "; //" Double.valueOf( "+val+" )";
    }
}
