package myfreeer.sql.extractor.formatter.impl;

import myfreeer.sql.extractor.properties.ExportProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@Component
public class TimestampFormatter extends DateFormatter {
  @Autowired
  public TimestampFormatter(final ExportProperties properties) {
    super(properties);
  }

  @Override
  public int type() {
    return Types.TIMESTAMP;
  }

  @Override
  public String format(final ResultSet resultSet, final int col) throws SQLException {
    final java.sql.Timestamp date = resultSet.getTimestamp(col);
    if (date == null) {
      return "null";
    }
    return format(date);
  }

  @Override
  protected String pattern() {
    return "yyyy-MM-dd HH:mm:ss.SSSSSS";
  }

  @Override
  protected String oraclePattern() {
    return "YYYY-MM-DD HH24:MI:SS.FF6";
  }

  @Override
  protected String conventFn() {
    return "TO_TIMESTAMP";
  }
}
