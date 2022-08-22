package utils.maths;

import java.math.BigDecimal;

public class Unit {

    private UnitCategory unitCategory;
    private String unitShort;
    private String unitName;
    private volatile BigDecimal factor;
    private volatile BigDecimal offset;
    private volatile boolean active;


    // ******************** Constructors **************************************
    public Unit(final UnitCategory UnitCATEGORY, final String UNIT_SHORT, final String UNIT_NAME, final double FACTOR) {
        this(UnitCATEGORY, UNIT_SHORT, UNIT_NAME, FACTOR, 0.0);
    }

    public Unit(final UnitCategory UnitCATEGORY, final String UNIT_SHORT, final String UNIT_NAME, final double FACTOR, final boolean ACTIVE) {
        this(UnitCATEGORY, UNIT_SHORT, UNIT_NAME, FACTOR, 0.0, ACTIVE);
    }

    public Unit(final UnitCategory UnitCATEGORY, final String UNIT_SHORT, final String UNIT_NAME, final BigDecimal FACTOR) {
        this(UnitCATEGORY, UNIT_SHORT, UNIT_NAME, FACTOR, new BigDecimal("0.0"), true);
    }

    public Unit(final UnitCategory UnitCATEGORY, final String UNIT_SHORT, final String UNIT_NAME, final BigDecimal FACTOR, final boolean ACTIVE) {
        this(UnitCATEGORY, UNIT_SHORT, UNIT_NAME, FACTOR, new BigDecimal("0.0"), ACTIVE);
    }

    public Unit(final UnitCategory UnitCATEGORY, final String UNIT_SHORT, final String UNIT_NAME, final double FACTOR, final double OFFSET) {
        this(UnitCATEGORY, UNIT_SHORT, UNIT_NAME, new BigDecimal(Double.toString(FACTOR)), new BigDecimal(Double.toString(OFFSET)), true);
    }

    public Unit(final UnitCategory UnitCATEGORY, final String UNIT_SHORT, final String UNIT_NAME, final double FACTOR, final double OFFSET, final boolean ACTIVE) {
        this(UnitCATEGORY, UNIT_SHORT, UNIT_NAME, new BigDecimal(Double.toString(FACTOR)), new BigDecimal(Double.toString(OFFSET)), ACTIVE);
    }

    public Unit(final UnitCategory UnitCATEGORY, final String UNIT_SHORT, final String UNIT_NAME, final BigDecimal FACTOR_BD, final BigDecimal OFFSET_BD) {
        this(UnitCATEGORY, UNIT_SHORT, UNIT_NAME, FACTOR_BD, OFFSET_BD, true);
    }

    public Unit(final UnitCategory UnitCATEGORY, final String UNIT_SHORT, final String UNIT_NAME, final BigDecimal FACTOR_BD, final BigDecimal OFFSET_BD, final boolean ACTIVE) {
        unitCategory = UnitCATEGORY;
        unitShort = UNIT_SHORT;
        unitName = UNIT_NAME;
        factor = FACTOR_BD;
        offset = OFFSET_BD;
        active = ACTIVE;
    }


    // ******************** Methods *******************************************
    public final UnitCategory getCategory() {
        return unitCategory;
    }

    public void setCategory(UnitCategory unitCategory) {
        this.unitCategory = unitCategory;
    }

    public final String getUnitShort() {
        return unitShort;
    }

    public final String getUnitName() {
        return unitName;
    }

    public final BigDecimal getFactor() {
        return factor;
    }

    public final void setFactor(final BigDecimal FACTOR) {
        factor = FACTOR;
    }

    public final void setFactor(final double FACTOR) {
        factor = new BigDecimal(Double.toString(FACTOR));
    }

    public final BigDecimal getOffset() {
        return offset;
    }

    public final void setOffset(final BigDecimal OFFSET) {
        offset = OFFSET;
    }

    public final void setOffset(final double OFFSET) {
        offset = new BigDecimal(Double.toString(OFFSET));
    }

    public final boolean isActive() {
        return active;
    }

    public final void setActive(final boolean ACTIVE) {
        active = ACTIVE;
    }

    @Override
    public final String toString() {
        return new StringBuilder().append(unitCategory)
                .append(" ")
                .append(unitShort)
                .append(" (")
                .append(unitName)
                .append(") ")
                .append(factor)
                .append(", ")
                .append(offset).toString();
    }

}
