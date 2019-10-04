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
        "    FUNCTION " + funcName + "(hex CLOB) RETURN BLOB IS\n" +
        "    b BLOB                := NULL;\n" +
        "    s VARCHAR2(4000 CHAR) := NULL;\n" +
        "    l NUMBER              := 4000;\n" +
        "BEGIN\n" +
        "    IF hex IS NOT NULL\n" +
        "    THEN\n" +
        "        dbms_lob.createtemporary(b, FALSE);\n" +
        "\n" +
        "        FOR i IN 0 .. LENGTH(hex) / 4000\n" +
        "            LOOP\n" +
        "                dbms_lob.read(hex, l, i * 4000 + 1, s);\n" +
        "                dbms_lob.append(b, to_blob(hextoraw(s)));\n" +
        "            END LOOP;\n" +
        "    END IF;\n" +
        "\n" +
        "    RETURN b;\n" +
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
