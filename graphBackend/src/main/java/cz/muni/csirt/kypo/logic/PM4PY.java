package cz.muni.csirt.kypo.logic;

/**
 * A class for creating svg via python library PM4PY
 */
public final class PM4PY {
    /**
     * Creates an svg file (outputSvg) from csv file (inputCsv) using PM4PY library
     * @param inputCsv csv file input
     * @param outputSvg svg file output
     */
    public static void createSVG(String inputCsv, String outputSvg) {
        try {
            Process p = Runtime.getRuntime().exec("python3 target/create_svg.py " + inputCsv + " " + outputSvg);
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
