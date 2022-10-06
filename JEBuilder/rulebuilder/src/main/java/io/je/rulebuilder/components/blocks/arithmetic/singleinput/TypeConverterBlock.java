package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.beans.UnifiedType;

public class TypeConverterBlock extends SingleInputArithmeticBlock {


    UnifiedType typeToConvertTo;
    String dateFormat;

    public TypeConverterBlock(BlockModel blockModel) {
        super(blockModel);
        typeToConvertTo = UnifiedType.valueOf((String) blockModel.getBlockConfiguration().get(AttributesMapping.VALUE));
        dateFormat = (String) blockModel.getBlockConfiguration().get(AttributesMapping.VALUE2);

        updateDefaultValue();

    }

    private void updateDefaultValue() {
        defaultType = typeToConvertTo;
    }

    private TypeConverterBlock() {

    }

    @Override
    protected String getFormula() {
        switch (typeToConvertTo) {
            case SBYTE:
                return "Byte.valueOf( ConversionUtilities.convertIfBoolean(\"\" + " + inputBlockLinks.get(0).getReference() + ") )";
            case UINT16:
            case INT32:
            case INT:
                return "Integer.valueOf( (int)Double.valueOf( ConversionUtilities.convertIfBoolean(\"\" + " + inputBlockLinks.get(0).getReference() + ") ) )";

            case BYTE:
            case INT16:
            case SHORT:
                return "Short.valueOf( ConversionUtilities.convertIfBoolean(\"\" + " + inputBlockLinks.get(0).getReference() + ") )";

            case UINT32:
            case INT64:
            case LONG:
                return "Long.valueOf( ConversionUtilities.convertIfBoolean(\"\" + " + inputBlockLinks.get(0).getReference() + ") )";

            case UINT64:
            case FLOAT:
            case SINGLE:
                return "Float.valueOf( ConversionUtilities.convertIfBoolean(\"\" + " + inputBlockLinks.get(0).getReference() + ") )";

            case DOUBLE:
                return "Double.valueOf( ConversionUtilities.convertIfBoolean(\"\" + " + inputBlockLinks.get(0).getReference() + ") )";

            case BOOL:
                return "Boolean.valueOf( ConversionUtilities.convertIfBoolean(\"\" + " + inputBlockLinks.get(0).getReference() + ") )";

            case OBJECT:
            case STRING:
                return "String.valueOf( ConversionUtilities.convertIfBoolean(\"\" + " + inputBlockLinks.get(0).getReference() + ") )";

            case DATETIME:
                return "ConversionUtilities.convertTypeDate(\"" + dateFormat + "\", \"\" + " + inputBlockLinks.get(0).getReference() + " )";


            default:
                //JELogger.error("Failed to set variable\""+this.jobEngineElementName+"\" value to "+value+": Incompatible Type", null, this.jobEngineProjectID, LogSubModule.VARIABLE, this.jobEngineElementID);
                return null;

        }

    }

    public UnifiedType getTypeToConvertTo() {
        return typeToConvertTo;
    }

    public void setTypeToConvertTo(String typeToConvertTo) {
        this.typeToConvertTo = UnifiedType.valueOf(typeToConvertTo);
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }


}
