package myfreeer.sql.extractor.util;

/**
 * Oracle JDBC Types
 */
public interface OracleTypes {
  /**
   * TIMESTAMP WITH TIME ZONE
   * oracle.jdbc.OracleTypes.TIMESTAMPTZ (ORACLE EXTENSION)
   */
  int TIMESTAMPTZ = -101;
  /**
   * TIMESTAMP WITH LOCAL TIME ZONE
   * oracle.jdbc.OracleTypes.TIMESTAMPLTZ (ORACLE EXTENSION)
   */
  int TIMESTAMPLTZ = -102;

  /**
   * BINARY_FLOAT
   * oracle.jdbc.OracleTypes.BINARY_FLOAT
   */
  int BINARY_FLOAT = 100;

  /**
   * BINARY_DOUBLE
   * oracle.jdbc.OracleTypes.BINARY_DOUBLE
   */
  int BINARY_DOUBLE = 101;
}
