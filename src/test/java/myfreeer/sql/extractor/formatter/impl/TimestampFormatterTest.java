package myfreeer.sql.extractor.formatter.impl;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimestampFormatterTest extends DateFormatterTest {
  private final TimestampFormatter formatter = new TimestampFormatter(properties);

  @Override
  protected TimestampFormatter formatter() {
    return formatter;
  }

  @Override
  public void type() {
    assertEquals(Types.TIMESTAMP, formatter().type());
  }

  @Override
  public void pattern() {
    assertEquals("yyyy-MM-dd HH:mm:ss.SSSSSS", formatter().pattern());
  }

  @Override
  public void conventFn() {
    assertEquals("TO_TIMESTAMP", formatter().conventFn());
  }

  @Override
  public void oraclePattern() {
    assertEquals("YYYY-MM-DD HH24:MI:SS.FF6", formatter().oraclePattern());
  }

  @Override
  public void formatResultSet() throws SQLException {
    final Date now = new Date();
    assertEquals(formatter().format(now), formatter().format(resultSet(now), 1));
  }

  @Override
  public void nullValue() throws SQLException {
    assertEquals("null", formatter().format(resultSet(null), 1));
  }
}
