package myfreeer.sql.extractor.util;

/**
 * temp function conventing clob to blob
 * for inserting blob in single insert statement
 */
public class OracleClobToBlob {
  /**
   * get clob to blob function creation sql script with specified name
   *
   * @param funcName specified function name
   * @return function creation sql script
   */
  public static String getFunc(final String funcName) {
    //language=Oracle
    return "CREATE OR REPLACE\n" +
        "    FUNCTION " + funcName + "(c IN CLOB) RETURN BLOB\n" +
        "    IS\n" +
        "    pos     PLS_INTEGER := 1;\n" +
        "    buffer  RAW(4000);\n" +
        "    res     BLOB;\n" +
        "    lob_len PLS_INTEGER := DBMS_LOB.getLength(c);\n" +
        "BEGIN\n" +
        "    DBMS_LOB.createTemporary(res, TRUE);\n" +
        "    DBMS_LOB.OPEN(res, DBMS_LOB.LOB_ReadWrite);\n" +
        "\n" +
        "    LOOP\n" +
        "        buffer := hextoraw(DBMS_LOB.SUBSTR(c, 4000, pos));\n" +
        "\n" +
        "        IF UTL_RAW.LENGTH(buffer) > 0\n" +
        "        THEN\n" +
        "            DBMS_LOB.writeAppend(res, UTL_RAW.LENGTH(buffer), buffer);\n" +
        "        END IF;\n" +
        "\n" +
        "        pos := pos + 4000;\n" +
        "        EXIT WHEN pos > lob_len;\n" +
        "    END LOOP;\n" +
        "\n" +
        "    RETURN res;\n" +
        "END " + funcName + ";\n";
  }

  /**
   * get clob to blob function drop sql script with specified name
   *
   * @param funcName specified function name
   * @return function drop sql script
   */
  public static String getDropFunc(final String funcName) {
    //language=Oracle
    return "\nDROP FUNCTION " + funcName + ";\n";
  }
}
