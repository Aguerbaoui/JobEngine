package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;

public class CalculationBlock extends SingleInputArithmeticBlock {

    int operationId = 0;
    protected String calculatorReference = "MathUtilities.";
    String value = null;

    public CalculationBlock(BlockModel blockModel) {
        super(blockModel);
        operationId = blockModel.getOperationId();
        if (blockModel.getBlockConfiguration() != null) {
            value = String.valueOf(blockModel.getBlockConfiguration().get(AttributesMapping.VALUE));
        }
    }

    private CalculationBlock() {


    }

    @Override
    protected String getFormula() {
        StringBuilder formula = new StringBuilder();

        String method = getMethod();
        formula.append(method);
        return formula.toString();
    }


    private String getMethod() {
        switch (operationId) {
            // Factorial
            case 1005:

                return calculatorReference + "factorial( " + getInputRefName(0) + ")";
            // Square
            case 1006:
                return calculatorReference + "square( " + getInputRefName(0) + ")";
            // SquareRoot
            case 1007:
                return calculatorReference + "sqrt( " + getInputRefName(0) + ")";
            // Power
            case 1008:
                return calculatorReference + "power( " + getInputRefName(0) + ", " + value + ")";
            // change sign
            case 1009:
                return calculatorReference + "changeSign( " + getInputRefName(0) + ")";
            //Bias
            case 1010:
                //return calculatorReference+"bias( " + getInputRefName(0)+ ","+value+")";

                return getInputRefName(0) + " + " + value;
            //gain
            case 1011:
                return getInputRefName(0) + " * " + value;
            //multiplicative inverse
            case 1012:
                return calculatorReference + "multiplicativeInverse( " + getInputRefName(0) + ")";
            case 9999:
                return calculatorReference + "multiplicativeManel( " + getInputRefName(0) + ", " + value + ")";
            //abs
            case 1013:
                return calculatorReference + "abs( " + getInputRefName(0) + ")";
            //exp
            case 1014:
                return calculatorReference + "exp( " + getInputRefName(0) + ")";
            //log10
            case 1015:
                return calculatorReference + "log10( " + getInputRefName(0) + ")";
            //tan
            case 1016:
                return calculatorReference + "tan( " + getInputRefName(0) + ")";
            //atan
            case 1017:
                return calculatorReference + "atan( " + getInputRefName(0) + ")";
            //acos
            case 1018:
                return calculatorReference + "acos( " + getInputRefName(0) + ")";
            //asin
            case 1019:
                return calculatorReference + "asin( " + getInputRefName(0) + ")";
            //floor
            case 1020:
                return calculatorReference + "floor( " + getInputRefName(0) + ")";
            //truncate (round)
            case 1021:
                return calculatorReference + "truncate( " + getInputRefName(0) + ")";
            //ceiling
            case 1022:
                return calculatorReference + "ceil( " + getInputRefName(0) + ")";
            //sin
            case 1023:
                return calculatorReference + "sin( " + getInputRefName(0) + ")";
            //cos
            case 1024:
                return calculatorReference + "cos( " + getInputRefName(0) + ")";
            //ln
            case 1025:
                return calculatorReference + "ln( " + getInputRefName(0) + ")";
            //length
            case 1026:
                return getInputRefName(0) + ".length()";
            default:
                return getInputRefName(0);

        }

    }


    @Override
    protected String evaluateExecution(String... inputs) {
        switch (operationId) {

            //factorial x>=0 && x<=20
            case 1005:
                return "eval(JEMathUtils.factorialConstraint(\"" + this.jobEngineProjectID + "\",\"" + this.ruleId + "\",\"" + this.blockName + "\"," + inputs[0] + "))\n";

            //sqrt x>=0
            case 1007:
                return "eval(JEMathUtils.positive(\"" + this.jobEngineProjectID + "\",\"" + this.ruleId + "\",\"" + this.blockName + "\"," + inputs[0] + "))\n";
            //ln x>0
            case 1025:
                return "eval(JEMathUtils.strictlyPositive(\"" + this.jobEngineProjectID + "\",\"" + this.ruleId + "\",\"" + this.blockName + "\"," + inputs[0] + "))\n";
            default:
                return "";
        }
    }

}
