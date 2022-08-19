package utils.maths;

import java.math.BigDecimal;

public enum UnitDefinition {

    // Length
    KILOMETER(new Unit(UnitCategory.LENGTH, "km", "Kilometer", new BigDecimal("1000.0"))),
    HECTOMETER(new Unit(UnitCategory.LENGTH, "hm", "Hectometer", new BigDecimal("100"))),
    METER(new Unit(UnitCategory.LENGTH, "m", "Meter", new BigDecimal("1.0"))),
    DECIMETER(new Unit(UnitCategory.LENGTH, "dm", "Decimeter", new BigDecimal("0.1"))),
    CENTIMETER(new Unit(UnitCategory.LENGTH, "cm", "Centimeter", new BigDecimal("0.01"))),
    MILLIMETER(new Unit(UnitCategory.LENGTH, "mm", "Millimeter", new BigDecimal("0.0010"))),
    MICROMETER(new Unit(UnitCategory.LENGTH, "\u00b5m", "Micrometer", new BigDecimal("1.0E-6"))),
    NANOMETER(new Unit(UnitCategory.LENGTH, "nm", "Nanometer", new BigDecimal("1.0E-9"))),
    ANGSTROM(new Unit(UnitCategory.LENGTH, "\u00c5", "Angstrom", new BigDecimal("1.0E-10"))),
    PICOMETER(new Unit(UnitCategory.LENGTH, "pm", "Picometer", new BigDecimal("1.0E-12"))),
    FEMTOMETER(new Unit(UnitCategory.LENGTH, "fm", "Femtometer", new BigDecimal("1.0E-15"))),
    INCHES(new Unit(UnitCategory.LENGTH, "in", "Inches", new BigDecimal("0.0254"))),
    MILES(new Unit(UnitCategory.LENGTH, "mi", "Miles", new BigDecimal("1609.344"))),
    NAUTICAL_MILES(new Unit(UnitCategory.LENGTH, "nmi", "Nautical Miles", new BigDecimal("1852.0"))),
    FEET(new Unit(UnitCategory.LENGTH, "ft", "Feet", new BigDecimal("0.3048"))),
    YARD(new Unit(UnitCategory.LENGTH, "yd", "Yard", new BigDecimal("0.9144"))),
    LIGHT_YEAR(new Unit(UnitCategory.LENGTH, "l.y.", "Light-Year", new BigDecimal("9.46073E15"))),
    PARSEC(new Unit(UnitCategory.LENGTH, "pc", "Parsec", new BigDecimal("3.085678E16"))),
    PIXEL(new Unit(UnitCategory.LENGTH, "px", "Pixel", new BigDecimal("0.000264583"))),
    POINT(new Unit(UnitCategory.LENGTH, "pt", "Point", new BigDecimal("0.0003527778"))),
    PICA(new Unit(UnitCategory.LENGTH, "p", "Pica", new BigDecimal("0.0042333333"))),
    EM(new Unit(UnitCategory.LENGTH, "em", "Quad", new BigDecimal("0.0042175176"))),

    // Mass
    TON(new Unit(UnitCategory.MASS, "t", "Ton", new BigDecimal("1.0E3"))),
    KILOGRAM(new Unit(UnitCategory.MASS, "kg", "Kilogram", new BigDecimal("1.0"))),
    GRAM(new Unit(UnitCategory.MASS, "g", "Gram", new BigDecimal("1.0E-3"))),
    MILLIGRAM(new Unit(UnitCategory.MASS, "mg", "Milligram", new BigDecimal("1.0E-6"))),
    MICROGRAM(new Unit(UnitCategory.MASS, "\u00b5g", "Mikrogram", new BigDecimal("1.0E-6"))),
    NANOGRAM(new Unit(UnitCategory.MASS, "ng", "Nanogram", new BigDecimal("1.0E-9"))),
    PICOGRAM(new Unit(UnitCategory.MASS, "pg", "Picogram", new BigDecimal("1.0E-12"))),
    FEMTOGRAM(new Unit(UnitCategory.MASS, "fg", "Femtogram", new BigDecimal("1.0E-15"))),
    OUNCE(new Unit(UnitCategory.MASS, "oz", "Ounce (US)", new BigDecimal("0.028"))),
    POUND(new Unit(UnitCategory.MASS, "lb", "Pounds (US)", new BigDecimal("0.45359237"))),

    // Time
    WEEK(new Unit(UnitCategory.TIME, "wk", "Week", new BigDecimal("604800"))),
    DAY(new Unit(UnitCategory.TIME, "d", "Day", new BigDecimal("86400"))),
    HOUR(new Unit(UnitCategory.TIME, "h", "Hour", new BigDecimal("3600"))),
    MINUTE(new Unit(UnitCategory.TIME, "m", "Minute", new BigDecimal("60"))),
    SECOND(new Unit(UnitCategory.TIME, "s", "Second", new BigDecimal("1.0"))),
    MILLISECOND(new Unit(UnitCategory.TIME, "ms", "Millisecond", new BigDecimal("1E-3"))),
    MICROSECOND(new Unit(UnitCategory.TIME, "\u00b5s", "Microsecond", new BigDecimal("1E-6"))),
    NANOSECOND(new Unit(UnitCategory.TIME, "ns", "Nanosecond", new BigDecimal("1E-9"))),
    PICOSECOND(new Unit(UnitCategory.TIME, "ps", "Picosecond", new BigDecimal("1E-12"))),
    FEMTOSECOND(new Unit(UnitCategory.TIME, "fs", "Femtosecond", new BigDecimal("1E-15"))),

    // Area
    SQUARE_KILOMETER(new Unit(UnitCategory.AREA, "km\u00b2", "Square Kilometer", new BigDecimal("1.0E6"))),
    SQUARE_METER(new Unit(UnitCategory.AREA, "m\u00b2", "Square Meter", new BigDecimal("1.0"))),
    SQUARE_CENTIMETER(new Unit(UnitCategory.AREA, "cm\u00b2", "Square Centimeter", new BigDecimal("1.0E-4"))),
    SQUARE_MILLIMETER(new Unit(UnitCategory.AREA, "mm\u00b2", "Square Millimeter", new BigDecimal("1.0E-6"))),
    SQUARE_MICROMETER(new Unit(UnitCategory.AREA, "\u00b5m\u00b2", "Square Mikrometer", new BigDecimal("1.0E-12"))),
    SQUARE_NANOMETER(new Unit(UnitCategory.AREA, "nm\u00b2", "Square Nanometer", new BigDecimal("1.0E-18"))),
    SQUARE_ANGSTROM(new Unit(UnitCategory.AREA, "\u00c5\u00b2", "Square \u00c5ngstrom", new BigDecimal("1.0E-20"))),
    SQUARE_PICOMETER(new Unit(UnitCategory.AREA, "pm\u00b2", "Square Picometer", new BigDecimal("1.0E-24"))),
    SQUARE_FEMTOMETER(new Unit(UnitCategory.AREA, "fm\u00b2", "Square Femtometer", new BigDecimal("1.0E-30"))),
    HECTARE(new Unit(UnitCategory.AREA, "ha", "Hectare", new BigDecimal("1.0E5"))),
    ACRE(new Unit(UnitCategory.AREA, "ac", "Acre", new BigDecimal("4046.8564224"))),
    ARES(new Unit(UnitCategory.AREA, "a", "Ares", new BigDecimal("100"))),
    SQUARE_INCH(new Unit(UnitCategory.AREA, "in\u00b2", "Square Inch", new BigDecimal("0.00064516"))),
    SQUARE_FOOT(new Unit(UnitCategory.AREA, "ft\u00b2", "Square Foot", new BigDecimal("0.09290304"))),

    // Temperature
    KELVIN(new Unit(UnitCategory.TEMPERATURE, "K", "Kelvin", new BigDecimal("1.0"))),
    CELSIUS(new Unit(UnitCategory.TEMPERATURE, "\u00b0C", "Celsius", new BigDecimal("1.0"), new BigDecimal("273.15"))),
    FAHRENHEIT(new Unit(UnitCategory.TEMPERATURE, "\u00b0F", "Fahrenheit", new BigDecimal("0.555555555555555"), new BigDecimal("459.67"))),

    // Angle
    DEGREE(new Unit(UnitCategory.ANGLE, "deg", "Degree", (Math.PI / 180.0))),
    RADIAN(new Unit(UnitCategory.ANGLE, "rad", "Radian", new BigDecimal("1.0"))),
    GRAD(new Unit(UnitCategory.ANGLE, "grad", "Gradian", new BigDecimal("0.9"))),

    // Volume
    CUBIC_MILLIMETER(new Unit(UnitCategory.VOLUME, "mm\u00b3", "Cubic Millimeter", new BigDecimal("1.0E-9"))),
    MILLILITER(new Unit(UnitCategory.VOLUME, "ml", "Milliliter", new BigDecimal("1.0E-6"))),
    LITER(new Unit(UnitCategory.VOLUME, "l", "Liter", new BigDecimal("1.0E-3"))),
    CUBIC_METER(new Unit(UnitCategory.VOLUME, "m\u00b3", "Cubic Meter", new BigDecimal("1.0E0"))),
    GALLON(new Unit(UnitCategory.VOLUME, "gal", "US Gallon", new BigDecimal("0.0037854118"))),
    CUBIC_FEET(new Unit(UnitCategory.VOLUME, "ft\u00b3", "Cubic Foot", new BigDecimal("0.0283168466"))),
    CUBIC_INCH(new Unit(UnitCategory.VOLUME, "in\u00b3", "Cubic Inch", new BigDecimal("0.0000163871"))),

    // Voltage
    MILLIVOLT(new Unit(UnitCategory.VOLTAGE, "mV", "Millivolt", new BigDecimal("1.0E-3"))),
    VOLT(new Unit(UnitCategory.VOLTAGE, "V", "Volt", new BigDecimal("1.0E0"))),
    KILOVOLT(new Unit(UnitCategory.VOLTAGE, "kV", "Kilovolt", new BigDecimal("1.0E3"))),
    MEGAVOLT(new Unit(UnitCategory.VOLTAGE, "MV", "Megavolt", new BigDecimal("1.0E6"))),

    // Current
    PICOAMPERE(new Unit(UnitCategory.CURRENT, "pA", "Picoampere", new BigDecimal("1.0E-12"))),
    NANOAMPERE(new Unit(UnitCategory.CURRENT, "nA", "Nanoampere", new BigDecimal("1.0E-9"))),
    MICROAMPERE(new Unit(UnitCategory.CURRENT, "\u00b5A", "Microampere", new BigDecimal("1.0E-6"))),
    MILLIAMPERE(new Unit(UnitCategory.CURRENT, "mA", "Milliampere", new BigDecimal("1.0E-3"))),
    AMPERE(new Unit(UnitCategory.CURRENT, "A", "Ampere", new BigDecimal("1.0"))),
    KILOAMPERE(new Unit(UnitCategory.CURRENT, "kA", "Kiloampere", new BigDecimal("1.0E3"))),

    // Speed
    MILLIMETER_PER_SECOND(new Unit(UnitCategory.SPEED, "mm/s", "Millimeter per second", new BigDecimal("1.0E-3"))),
    METER_PER_SECOND(new Unit(UnitCategory.SPEED, "m/s", "Meter per second", new BigDecimal("1.0E0"))),
    KILOMETER_PER_HOUR(new Unit(UnitCategory.SPEED, "km/h", "Kilometer per hour", new BigDecimal("0.2777777778"))),
    MILES_PER_HOUR(new Unit(UnitCategory.SPEED, "mph", "Miles per hour", new BigDecimal("0.4472271914"))),
    KNOT(new Unit(UnitCategory.SPEED, "kt", "Knot", new BigDecimal("0.51444444444444"))),
    MACH(new Unit(UnitCategory.SPEED, "M", "Mach", new BigDecimal("0.00293866995797"))),

    // TemperatureGradient
    KELVIN_PER_SECOND(new Unit(UnitCategory.TEMPERATURE_GRADIENT, "K/s", "Kelvin per second", new BigDecimal("1.0"))),
    KELVIN_PER_MINUTE(new Unit(UnitCategory.TEMPERATURE_GRADIENT, "K/min", "Kelvin per minute", new BigDecimal("0.0166666667"))),
    KEVLIN_PER_HOUR(new Unit(UnitCategory.TEMPERATURE_GRADIENT, "K/h", "Kelvin per hour", new BigDecimal("0.0002777778"))),

    // ElectricCharge
    ELEMENTARY_CHARGE(new Unit(UnitCategory.ELECTRIC_CHARGE, "e-", "Elementary charge", new BigDecimal("1.6022E-19"))),
    PICOCOULOMB(new Unit(UnitCategory.ELECTRIC_CHARGE, "pC", "Picocoulomb", new BigDecimal("1.0E-12"))),
    NANOCOULOMB(new Unit(UnitCategory.ELECTRIC_CHARGE, "nC", "Nanocoulomb", new BigDecimal("1.0E-9"))),
    MICROCOULOMB(new Unit(UnitCategory.ELECTRIC_CHARGE, "\u00b5C", "Microcoulomb", new BigDecimal("1.0E-6"))),
    MILLICOULOMB(new Unit(UnitCategory.ELECTRIC_CHARGE, "mC", "Millicoulomb", new BigDecimal("1.0E-3"))),
    COULOMB(new Unit(UnitCategory.ELECTRIC_CHARGE, "C", "Coulomb", new BigDecimal("1.0E0"))),

    // Energy
    MILLIJOULE(new Unit(UnitCategory.ENERGY, "mJ", "Millijoule", new BigDecimal("1.0E-3"))),
    JOULE(new Unit(UnitCategory.ENERGY, "J", "Joule", new BigDecimal("1.0E0"))),
    KILOJOULE(new Unit(UnitCategory.ENERGY, "kJ", "Kilojoule", new BigDecimal("1.0E3"))),
    MEGAJOULE(new Unit(UnitCategory.ENERGY, "MJ", "Megajoule", new BigDecimal("1.0E6"))),
    CALORY(new Unit(UnitCategory.ENERGY, "cal", "Calory", new BigDecimal("4.1868"))),
    KILOCALORY(new Unit(UnitCategory.ENERGY, "kcal", "Kilocalory", new BigDecimal("4186.8"))),
    WATT_SECOND(new Unit(UnitCategory.ENERGY, "W*s", "Watt second", new BigDecimal("1.0E0"))),
    WATT_HOUR(new Unit(UnitCategory.ENERGY, "W*h", "Watt hour", new BigDecimal("3600"))),
    KILOWATT_SECOND(new Unit(UnitCategory.ENERGY, "kW*s", "Kilowatt second", new BigDecimal("1000"))),
    KILOWATT_HOUR(new Unit(UnitCategory.ENERGY, "kW*h", "Kilowatt hour", new BigDecimal("3600000"))),
    BTU(new Unit(UnitCategory.ENERGY, "btu", "British thermal unit", new BigDecimal("1055.055853"))),


    // Force
    NEWTON(new Unit(UnitCategory.FORCE, "N", "Newton", new BigDecimal("1.0"))),
    KILOGRAM_FORCE(new Unit(UnitCategory.FORCE, "kp", "Kilogram-Force", new BigDecimal("9.80665"))),
    POUND_FORCE(new Unit(UnitCategory.FORCE, "lbf", "Pound-Force", new BigDecimal("4.4482216153"))),

    // Humidity
    PERCENTAGE(new Unit(UnitCategory.HUMIDITY, "%", "Percentage", new BigDecimal(1.0))),

    // Acceleration
    METER_PER_SQUARE_SECOND(new Unit(UnitCategory.ACCELERATION, "m/s\u00b2", "Meter per squaresecond", new BigDecimal("1.0E0"))),
    INCH_PER_SQUARE_SECOND(new Unit(UnitCategory.ACCELERATION, "in/s\u00b2", "Inch per squaresecond", new BigDecimal("0.0254"))),
    GRAVITY(new Unit(UnitCategory.ACCELERATION, "g", "Gravity", new BigDecimal("9.80665"))),

    // Pressure
    MILLIPASCAL(new Unit(UnitCategory.PRESSURE, "mPa", "Millipascal", new BigDecimal("1.0E-3"))),
    PASCAL(new Unit(UnitCategory.PRESSURE, "Pa", "Pascal", new BigDecimal("1.0E0"))),
    HECTOPASCAL(new Unit(UnitCategory.PRESSURE, "hPa", "Hectopascal", new BigDecimal("1.0E2"))),
    KILOPASCAL(new Unit(UnitCategory.PRESSURE, "kPa", "Kilopascal", new BigDecimal("1.0E3"))),
    BAR(new Unit(UnitCategory.PRESSURE, "bar", "Bar", new BigDecimal("1.0E5"))),
    MILLIBAR(new Unit(UnitCategory.PRESSURE, "mbar", "Millibar", new BigDecimal("1.0E2"))),
    TORR(new Unit(UnitCategory.PRESSURE, "Torr", "Torr", new BigDecimal("133.322368421"))),
    PSI(new Unit(UnitCategory.PRESSURE, "psi", "Pound per Square Inch", new BigDecimal("6894.757293178"))),
    PSF(new Unit(UnitCategory.PRESSURE, "psf", "Pound per Square Foot", new BigDecimal("47.88026"))),
    ATMOSPHERE(new Unit(UnitCategory.PRESSURE, "atm", "Atmosphere", new BigDecimal("101325.0"))),

    // Torque
    NEWTON_METER(new Unit(UnitCategory.TORQUE, "Nm", "Newton Meter", new BigDecimal("1.0"))),
    METER_KILOGRAM(new Unit(UnitCategory.TORQUE, "m kg", "Meter Kilogram", new BigDecimal("0.101971621"))),
    FOOT_POUND_FORCE(new Unit(UnitCategory.TORQUE, "ft lbf", "Foot-Pound Force", new BigDecimal("1.3558179483"))),
    INCH_POUND_FORCE(new Unit(UnitCategory.TORQUE, "in lbf", "Inch-Pound Force", new BigDecimal("0.112984829"))),

    // Data
    BIT(new Unit(UnitCategory.DATA, "b", "Bit", new BigDecimal("1.0"))),
    KILOBIT(new Unit(UnitCategory.DATA, "Kb", "KiloBit", new BigDecimal("1024"))),
    MEGABIT(new Unit(UnitCategory.DATA, "Mb", "Megabit", new BigDecimal("1048576"))),
    GIGABIT(new Unit(UnitCategory.DATA, "Gb", "Gigabit", new BigDecimal("1073741824"))),
    BYTE(new Unit(UnitCategory.DATA, "B", "Byte", new BigDecimal("8"))),
    KILOBYTE(new Unit(UnitCategory.DATA, "KB", "Kilobyte", new BigDecimal("8192"))),
    MEGABYTE(new Unit(UnitCategory.DATA, "MB", "Megabyte", new BigDecimal("8388608"))),
    GIGABYTE(new Unit(UnitCategory.DATA, "GB", "Gigabyte", new BigDecimal("8.589934592E9"))),
    TERABYTE(new Unit(UnitCategory.DATA, "TB", "Terabyte", new BigDecimal("8.796093E12"))),

    // Luminance
    CANDELA_SQUARE_METER(new Unit(UnitCategory.LUMINANCE, "cd/m\u00b2", "Candela per Square Meter", new BigDecimal("1.0"))),
    CANDELA_SQUARE_CENTIMETER(new Unit(UnitCategory.LUMINANCE, "cd/cm\u00b2", "Candela per Square CentiMeter", new BigDecimal("10000.0"))),
    CANDELA_SQUARE_INCH(new Unit(UnitCategory.LUMINANCE, "cd/in\u00b2", "Candela per Square Inch", new BigDecimal("1550.0031"))),
    CANDELA_SQAURE_FOOT(new Unit(UnitCategory.LUMINANCE, "cd/ft\u00b2", "Candela per Square Foot", new BigDecimal("10.7639104167"))),
    LAMBERT(new Unit(UnitCategory.LUMINANCE, "L", "Lambert", new BigDecimal("3183.09886183"))),
    FOOT_LAMBERT(new Unit(UnitCategory.LUMINANCE, "fL", "Footlambert", new BigDecimal("3.4262590996"))),

    // Luminous flux
    LUX(new Unit(UnitCategory.LUMINOUS_FLUX, "lm/m\u00b2", "Lux", new BigDecimal("1.0"))),
    PHOT(new Unit(UnitCategory.LUMINOUS_FLUX, "lm/cm\u00b2", "Phot", new BigDecimal("10000.0"))),
    FOOT_CANDLE(new Unit(UnitCategory.LUMINOUS_FLUX, "lm/ft\u00b2", "Footcandle", new BigDecimal("10.7639104167"))),
    LUMEN_SQUARE_INCH(new Unit(UnitCategory.LUMINOUS_FLUX, "lm/in\u00b2", "Lumen per Square Inch", new BigDecimal("1550.0031"))),

    // Work
    MILLIWATT(new Unit(UnitCategory.WORK, "mW", "Milliwatt", new BigDecimal("1.0E-3"))),
    WATT(new Unit(UnitCategory.WORK, "W", "Watt", new BigDecimal("1.0E0"))),
    KILOWATT(new Unit(UnitCategory.WORK, "kW", "Kilowatt", new BigDecimal("1.0E3"))),
    MEGAWATT(new Unit(UnitCategory.WORK, "MW", "Megawatt", new BigDecimal("1.0E6"))),
    GIGAWATT(new Unit(UnitCategory.WORK, "GW", "Gigawatt", new BigDecimal("1.0E9"))),
    HORSEPOWER(new Unit(UnitCategory.WORK, "hp", "Horsepower", new BigDecimal("735.49875"))),
    JOULE_PER_SECOND(new Unit(UnitCategory.WORK, "J/s", "Joule per second", new BigDecimal("1.0E0"))),

    // Css Units
    PX(new Unit(UnitCategory.CSS_UNITS, "px", "Pixel", new BigDecimal("1.0"))),
    PT(new Unit(UnitCategory.CSS_UNITS, "pt", "Point", new BigDecimal("0.75"))),

    // Blood Glucose
    MILLIGRAM_PER_DECILITER(new Unit(UnitCategory.BLOOD_GLUCOSE, "mg/dl", "Milligram per deciliter", new BigDecimal("0.0555"))),
    MILLIMOL_PER_LITER(new Unit(UnitCategory.BLOOD_GLUCOSE, "mmol/l", "Millimols per liter", new BigDecimal("1.0"))),


    /*
     * offset x : 1 newUnit = x baseUnit
     */
    //DENSITY
    KILOGRAM_PER_CUBIC_METER(new Unit(UnitCategory.DENSITY, "kg/m\u00b3", "Kilogram per cubic meter", new BigDecimal("1.0E0"))),
    GRAM_PER_CUBIC_CENTIMETER(new Unit(UnitCategory.DENSITY, "g/cm\u00b3", "gram per cubic centimeter", new BigDecimal("1.0E3"))),
    GRAM_PER_CUBIC_METER(new Unit(UnitCategory.DENSITY, "g/m\u00b3", "Gram per cubic meter", new BigDecimal("1.0E-3"))),
    MILLIGRAM_PER_CUBIC_METER(new Unit(UnitCategory.DENSITY, "mg/m\u00b3", "Milligram per cubic meter", new BigDecimal("1.0E-6"))),
    POUND_PER_CUBIC_FOOT(new Unit(UnitCategory.DENSITY, "ib/ft\u00b3", "Pound per cubic foot", new BigDecimal("16.01846352"))),
    OUNCE_PER_GALLON(new Unit(UnitCategory.DENSITY, "oz/gal", "Ounce per gallon", new BigDecimal("7.489151776"))),

    //FLOW
    KILOGRAM_PER_SECOND(new Unit(UnitCategory.FLOW, "kg/s", "Kilogram per second", new BigDecimal("1.0E0"))),
    KILOGRAM_PER_HOUR(new Unit(UnitCategory.FLOW, "kg/h", "Kilogram per hour", new BigDecimal("0.00027778"))),
    CUBIC_FOOT_PER_MINUTE(new Unit(UnitCategory.FLOW, "ft\u00b3/mn", "Cubic foot per minute", new BigDecimal("0.35"))),
    CUBIC_METER_PER_SECOND(new Unit(UnitCategory.FLOW, "m\u00b3/s", "Cubic meter per second", new BigDecimal("739.33"))),
    GALLON_UPS_PER_MINUTE(new Unit(UnitCategory.FLOW, "gal/mn", "Gallon US per minute", new BigDecimal("0.047"))),


    //VISCOSITY
    CENTIPOISE(new Unit(UnitCategory.FLOW, "cP", "Centipoise", new BigDecimal("1.0E-3"))),
    KILOGRAM_PER_METER_SECOND(new Unit(UnitCategory.FLOW, "kg/(m.s)", "Kilogram per meter second", new BigDecimal("1.0E0"))),
    POUND_PER_FOOT_HOUR(new Unit(UnitCategory.FLOW, "lb/(ft.h)", "Pound per foot hour", new BigDecimal("0.00041")));


    public final Unit UNIT;

    UnitDefinition(final Unit UNIT) {
        this.UNIT = UNIT;
    }

}
