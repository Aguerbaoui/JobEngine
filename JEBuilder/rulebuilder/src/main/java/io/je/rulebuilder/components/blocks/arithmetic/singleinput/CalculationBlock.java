package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;

public class CalculationBlock extends SingleInputArithmeticBlock {

    protected String calculatorReference = "MathUtilities.";
    int operationId = 0;
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

                return calculatorReference + "factorial( " + asDouble(getInputReferenceByOrder(0)) + ")";
            // Square
            case 1006:
                return calculatorReference + "square( " + asDouble(getInputReferenceByOrder(0)) + ")";
            // SquareRoot
            case 1007:
                return calculatorReference + "sqrt( " + asDouble(getInputReferenceByOrder(0)) + ")";
            // Power
            case 1008:
                return calculatorReference + "power( " + asDouble(getInputReferenceByOrder(0)) + ", " + value + ")";
            // change sign
            case 1009:
                return calculatorReference + "changeSign( " + asDouble(getInputReferenceByOrder(0)) + ")";
            //Bias
            case 1010:
                return calculatorReference + "bias( " + asDouble(getInputReferenceByOrder(0)) + "," + value + ")";

            // return getInputReferenceByOrder(0) + " + " + value;
            //gain
            case 1011:
                return calculatorReference + "gain( " + asDouble(getInputReferenceByOrder(0)) + "," + value + ")";

            //multiplicative inverse
            case 1012:
                return calculatorReference + "multiplicativeInverse( " + asDouble(getInputReferenceByOrder(0)) + ")";
            //abs
            case 1013:
                return calculatorReference + "abs( " + asDouble(getInputReferenceByOrder(0)) + ")";
            //exp
            case 1014:
                return calculatorReference + "exp( " + asDouble(getInputReferenceByOrder(0)) + ")";
            //log10
            case 1015:
                return calculatorReference + "log10( " + asDouble(getInputReferenceByOrder(0)) + ")";
            //tan
            case 1016:
                return calculatorReference + "tan( " + asDouble(getInputReferenceByOrder(0)) + ")";
            //atan
            case 1017:
                return calculatorReference + "atan( " + asDouble(getInputReferenceByOrder(0)) + ")";
            //acos
            case 1018:
                return calculatorReference + "acos( " + asDouble(getInputReferenceByOrder(0)) + ")";
            //asin
            case 1019:
                return calculatorReference + "asin( " + asDouble(getInputReferenceByOrder(0)) + ")";
            //floor
            case 1020:
                return calculatorReference + "floor( " + asDouble(getInputReferenceByOrder(0)) + ")";
            //truncate (round)
            case 1021:
                return calculatorReference + "truncate( " + asDouble(getInputReferenceByOrder(0)) + ")";
            //ceiling
            case 1022:
                return calculatorReference + "ceil( " + asDouble(getInputReferenceByOrder(0)) + ")";
            //sin
            case 1023:
                return calculatorReference + "sin( " + asDouble(getInputReferenceByOrder(0)) + ")";
            //cos
            case 1024:
                return calculatorReference + "cos( " + asDouble(getInputReferenceByOrder(0)) + ")";
            //ln
            case 1025:
                return calculatorReference + "ln( " + asDouble(getInputReferenceByOrder(0)) + ")";
            //length
            case 1026:
                return " ( (Double) (( (String) " + getInputReferenceByOrder(0) + " ).length()) ) ";
            default:
                return getInputReferenceByOrder(0);

        }

    }


    @Override
    protected String evaluateExecution(String... inputs) {

        // FIXME why Varargs inputs while using just the first one

        switch (operationId) {

            // FIXME eval reduce Drools performance

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
