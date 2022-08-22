package utils.maths;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UnitConverter {

    public static final String[] ABBREVIATIONS = {"k", "M", "G", "T", "P", "E", "Z", "Y"};
    public static final int MAX_NO_OF_DECIMALS = 12;
    private static final EnumMap<UnitCategory, UnitDefinition> BASE_UNITS = new EnumMap<UnitCategory, UnitDefinition>(UnitCategory.class) {
        {
            put(UnitCategory.ACCELERATION, UnitDefinition.METER_PER_SQUARE_SECOND);
            put(UnitCategory.ANGLE, UnitDefinition.RADIAN);
            put(UnitCategory.AREA, UnitDefinition.SQUARE_METER);
            put(UnitCategory.CURRENT, UnitDefinition.AMPERE);
            put(UnitCategory.DATA, UnitDefinition.BIT);
            put(UnitCategory.ELECTRIC_CHARGE, UnitDefinition.ELEMENTARY_CHARGE);
            put(UnitCategory.ENERGY, UnitDefinition.JOULE);
            put(UnitCategory.FORCE, UnitDefinition.NEWTON);
            put(UnitCategory.HUMIDITY, UnitDefinition.PERCENTAGE);
            put(UnitCategory.LENGTH, UnitDefinition.METER);
            put(UnitCategory.LUMINANCE, UnitDefinition.CANDELA_SQUARE_METER);
            put(UnitCategory.LUMINOUS_FLUX, UnitDefinition.LUX);
            put(UnitCategory.MASS, UnitDefinition.KILOGRAM);
            put(UnitCategory.PRESSURE, UnitDefinition.PASCAL);
            put(UnitCategory.SPEED, UnitDefinition.METER_PER_SECOND);
            put(UnitCategory.TEMPERATURE, UnitDefinition.KELVIN);
            put(UnitCategory.TEMPERATURE_GRADIENT, UnitDefinition.KELVIN_PER_SECOND);
            put(UnitCategory.TIME, UnitDefinition.SECOND);
            put(UnitCategory.TORQUE, UnitDefinition.NEWTON_METER);
            put(UnitCategory.VOLUME, UnitDefinition.CUBIC_METER);
            put(UnitCategory.VOLTAGE, UnitDefinition.VOLT);
            put(UnitCategory.WORK, UnitDefinition.WATT);
            put(UnitCategory.BLOOD_GLUCOSE, UnitDefinition.MILLIMOL_PER_LITER);
            put(UnitCategory.DENSITY, UnitDefinition.KILOGRAM_PER_CUBIC_METER);
            put(UnitCategory.FLOW, UnitDefinition.KILOGRAM_PER_SECOND);
            put(UnitCategory.VISCOSITY, UnitDefinition.KILOGRAM_PER_METER_SECOND);
        }
    };
    private UnitDefinition baseUnitDefinition;
    private Unit bean;
    private Locale locale;
    private int decimals;
    private String formatString;

    // ******************** Constructors **************************************
    public UnitConverter(final UnitCategory UNIT_TYPE) {
        this(UNIT_TYPE, BASE_UNITS.get(UNIT_TYPE));
    }

    public UnitConverter(final UnitCategory UNIT_TYPE, final UnitDefinition BASE_UNIT_DEFINITION) {
        baseUnitDefinition = BASE_UNIT_DEFINITION;
        bean = BASE_UNITS.get(UNIT_TYPE).UNIT;
        locale = Locale.US;
        decimals = 2;
        formatString = "%.2f";
    }

    public static final String format(final double NUMBER, final int DECIMALS) {
        return format(NUMBER, clamp(0, 12, DECIMALS), Locale.US);
    }

    public static final String format(final double NUMBER, final int DECIMALS, final Locale LOCALE) {
        String formatString = new StringBuilder("%.").append(clamp(0, 12, DECIMALS)).append("f").toString();
        double value;
        for (int i = ABBREVIATIONS.length - 1; i >= 0; i--) {
            value = Math.pow(1000, i + 1);
            if (Double.compare(NUMBER, -value) <= 0 || Double.compare(NUMBER, value) >= 0) {
                return String.format(LOCALE, formatString, (NUMBER / value)) + ABBREVIATIONS[i];
            }
        }
        return String.format(LOCALE, formatString, NUMBER);
    }

    private static int clamp(final int MIN, final int MAX, final int VALUE) {
        if (VALUE < MIN) return MIN;
        if (VALUE > MAX) return MAX;
        return VALUE;
    }

    // ******************** Methods *******************************************
    public UnitCategory getUnitType() {
        return bean.getCategory();
    }

    public UnitDefinition getBaseUnitDefinition() {
        return baseUnitDefinition;
    }

    public void setBaseUnitDefinition(final UnitDefinition BASE_UNIT_DEFINITION) {
        if (BASE_UNIT_DEFINITION.UNIT.getCategory() == getUnitType()) {
            baseUnitDefinition = BASE_UNIT_DEFINITION;
        }
    }

    public BigDecimal getFactor() {
        return bean.getFactor();
    }

    public BigDecimal getOffset() {
        return bean.getOffset();
    }

    public String getUnitName() {
        return bean.getUnitName();
    }

    public String getUnitShort() {
        return bean.getUnitShort();
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(final Locale LOCALE) {
        locale = LOCALE;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(final int DECIMALS) {
        if (DECIMALS < 0) {
            decimals = 0;
        } else if (DECIMALS > MAX_NO_OF_DECIMALS) {
            decimals = MAX_NO_OF_DECIMALS;
        } else {
            decimals = DECIMALS;
        }
        formatString = new StringBuilder("%.").append(decimals).append("f").toString();
    }

    public String getFormatString() {
        return formatString;
    }

    public final boolean isActive() {
        return bean.isActive();
    }

    public final void setActive(final boolean ACTIVE) {
        bean.setActive(ACTIVE);
    }

    public final double convert(final double VALUE, final UnitDefinition UNIT_DEFINITION) {
        if (UNIT_DEFINITION.UNIT.getCategory() != getUnitType()) {
            throw new IllegalArgumentException("units have to be of the same type");
        }
        return ((((VALUE + baseUnitDefinition.UNIT.getOffset().doubleValue()) * baseUnitDefinition.UNIT.getFactor().doubleValue()) + bean.getOffset().doubleValue()) * bean.getFactor().doubleValue()) / UNIT_DEFINITION.UNIT
                .getFactor().doubleValue() - UNIT_DEFINITION.UNIT.getOffset().doubleValue();
    }

    public final String convertToString(final double VALUE, final UnitDefinition UNIT_DEFINITION) {
        return String.join(" ", String.format(locale, formatString, convert(VALUE, UNIT_DEFINITION)), UNIT_DEFINITION.UNIT.getUnitShort());
    }

    public final double convertToBaseUnit(final double VALUE, final UnitDefinition UNIT_DEFINITION) {
        return ((((VALUE + UNIT_DEFINITION.UNIT.getOffset().doubleValue()) * UNIT_DEFINITION.UNIT.getFactor().doubleValue()) + bean.getOffset().doubleValue()) * bean.getFactor().doubleValue()) / baseUnitDefinition.UNIT
                .getFactor().doubleValue() - baseUnitDefinition.UNIT.getOffset().doubleValue();
    }

    public final Pattern getPattern() {
        final StringBuilder PATTERN_BUILDER = new StringBuilder();
        PATTERN_BUILDER.append("^([-+]?\\d*\\.?\\d*)\\s?(");

        for (UnitDefinition unitDefinition : UnitDefinition.values()) {
            PATTERN_BUILDER.append(unitDefinition.UNIT.getUnitShort().replace("*", "\\*")).append("|");
        }

        PATTERN_BUILDER.deleteCharAt(PATTERN_BUILDER.length() - 1);

        //PATTERN_BUILDER.append("){1}$");
        PATTERN_BUILDER.append(")?$");

        return Pattern.compile(PATTERN_BUILDER.toString());
    }

    public final List<Unit> getAvailableUnits(final UnitCategory UNIT_DEFINITION) {
        return getAllUnitDefinitions().get(UNIT_DEFINITION).stream().map(unitDefinition -> unitDefinition.UNIT).collect(Collectors.toList());
    }

    public final EnumMap<UnitCategory, ArrayList<UnitDefinition>> getAllUnitDefinitions() {
        final EnumMap<UnitCategory, ArrayList<UnitDefinition>> UNIT_TYPES = new EnumMap<>(UnitCategory.class);
        final ArrayList<UnitCategory> UnitCATEGORY_LIST = new ArrayList<>(UnitCategory.values().length);
        UnitCATEGORY_LIST.addAll(Arrays.asList(UnitCategory.values()));
        UnitCATEGORY_LIST.forEach(unitCategory -> UNIT_TYPES.put(unitCategory, new ArrayList<>()));
        for (UnitDefinition unitDefinition : UnitDefinition.values()) {
            UNIT_TYPES.get(unitDefinition.UNIT.getCategory()).add(unitDefinition);
        }
        return UNIT_TYPES;
    }

    public final EnumMap<UnitCategory, ArrayList<UnitDefinition>> getAllActiveUnitDefinitions() {
        final EnumMap<UnitCategory, ArrayList<UnitDefinition>> UNIT_DEFINITIONS = new EnumMap<>(UnitCategory.class);
        final ArrayList<UnitCategory> UnitCATEGORY_LIST = new ArrayList<>(UnitCategory.values().length);
        UnitCATEGORY_LIST.addAll(Arrays.asList(UnitCategory.values()));
        UnitCATEGORY_LIST.forEach(unitCategory -> UNIT_DEFINITIONS.put(unitCategory, new ArrayList<>()));
        for (UnitDefinition unitDefinition : UnitDefinition.values()) {
            if (unitDefinition.UNIT.isActive()) {
                UNIT_DEFINITIONS.get(unitDefinition.UNIT.getCategory()).add(unitDefinition);
            }
        }
        return UNIT_DEFINITIONS;
    }

    @Override
    public String toString() {
        return getUnitType().toString();
    }


}
