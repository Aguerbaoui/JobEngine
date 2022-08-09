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
        if (typeToConvertTo.equalsIgnoreCase("string")) {
            defaultType = "string";
        } else if (typeToConvertTo.equalsIgnoreCase("date")) {
            defaultType = "date";

        }

    }

    @Override
    protected String getFormula() {
        if (typeToConvertTo.equalsIgnoreCase("string")) {
            return "String.valueOf(" + inputBlocks.get(0).getReference() + ")";
        } else if (typeToConvertTo.equalsIgnoreCase("date")) {
            return "ConversionUtilities.convertTypeDate(\"" + dateFormat + "\"," + inputBlocks.get(0).getReference() + ")";
        } else {
            return "Double.valueOf(" + inputBlocks.get(0).getReference() + ")";
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
