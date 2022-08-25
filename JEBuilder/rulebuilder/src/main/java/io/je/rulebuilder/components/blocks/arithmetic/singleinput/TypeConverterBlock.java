package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;

public class TypeConverterBlock extends SingleInputArithmeticBlock {


    String typeToConvertTo;
    String dateFormat;

    public TypeConverterBlock(BlockModel blockModel) {
        super(blockModel);
        typeToConvertTo = (String) blockModel.getBlockConfiguration().get(AttributesMapping.VALUE);
        dateFormat = (String) blockModel.getBlockConfiguration().get(AttributesMapping.VALUE2);

        updateDefaultValue();

    }

    private TypeConverterBlock() {

    }

    private void updateDefaultValue() {
        defaultType = typeToConvertTo;
    }

    @Override
    protected String getFormula() {
        // TODO add convert to Boolean ?
        // FIXME convert boolean to int, float, double, ...
        if (typeToConvertTo.equalsIgnoreCase("string")) {
            return "String.valueOf(\"\" + " + inputBlockLinks.get(0).getReference() + " )";
        } else if (typeToConvertTo.equalsIgnoreCase("date")) {
            return "ConversionUtilities.convertTypeDate(\"" + dateFormat + "\", \"\" + " + inputBlockLinks.get(0).getReference() + " )";
        } else if (typeToConvertTo.equalsIgnoreCase("int")) {
            // FIXME case input exceeds Integer range, not int (double, ...), case contains chars ...
            return "Integer.valueOf( (int)Double.valueOf(\"\" + " + inputBlockLinks.get(0).getReference() + " ) )";
        } else if (typeToConvertTo.equalsIgnoreCase("float")) {
            // FIXME case input exceeds Float range, case contains chars ...
            return "Float.valueOf(\"\" + " + inputBlockLinks.get(0).getReference() + " )";
        } else {
            // FIXME case input exceeds Double range, case contains chars ...
            return "Double.valueOf(\"\" + " + inputBlockLinks.get(0).getReference() + " )";
        }
    }

    public String getTypeToConvertTo() {
        return typeToConvertTo;
    }

    public void setTypeToConvertTo(String typeToConvertTo) {
        this.typeToConvertTo = typeToConvertTo;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }


}
