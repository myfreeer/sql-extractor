package myfreeer.sql.extractor.formatter.impl;

import myfreeer.sql.extractor.formatter.Formatter;
import myfreeer.sql.extractor.properties.ExportProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

@Component
public class DateFormatter implements Formatter<Date> {
  private final ZoneId zone;
  private final Locale locale;
  private final DateTimeFormatter formatter;

  @Autowired
  public DateFormatter(final ExportProperties properties) {
    zone = properties.getTimeZone();
    locale = properties.getLocale();
    formatter = DateTimeFormatter
        .ofPattern(pattern(), locale())
        .withZone(zone());
  }

  protected String pattern() {
    return "yyyy-MM-dd HH:mm:ss";
  }

  protected String conventFn() {
    return "TO_DATE";
  }

  protected String oraclePattern() {
    return "YYYY-MM-DD HH24:MI:SS";
  }

  protected ZoneId zone() {
    return zone;
  }

  protected Locale locale() {
    return locale;
  }

  @Override
  public int type() {
    return Types.DATE;
  }

  @Override
  public String format(final ResultSet resultSet, final int col) throws SQLException {
    final java.sql.Date date = resultSet.getDate(col);
    if (date == null) {
      return "null";
    }
    return format(date);
  }

  protected String format(final Date date) {
    return conventFn() + "('" +
        formatter.format(date instanceof java.sql.Date || date instanceof java.sql.Time ?
                Instant.ofEpochMilli(date.getTime()) :
                date.toInstant()) +
        "','" + oraclePattern() + "')";
  }
}
