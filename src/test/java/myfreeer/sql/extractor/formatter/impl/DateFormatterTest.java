package myfreeer.sql.extractor.formatter.impl;

import myfreeer.sql.extractor.properties.ExportProperties;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateFormatterTest {
  protected ExportProperties properties = new ExportProperties()
      .setLocale(Locale.ENGLISH)
      .setTimeZone(ZoneId.systemDefault());
  private final DateFormatter formatter = new DateFormatter(properties);

  protected DateFormatter formatter() {
    return formatter;
  }

  protected ResultSet resultSet(final Date data) throws SQLException {
    final ResultSet resultSet = Mockito.mock(ResultSet.class);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    if (data == null) {
      Mockito.when(resultSet.getDate(Mockito.anyInt()))
          .thenReturn(null).thenReturn(null);
      Mockito.when(resultSet.getTimestamp(Mockito.anyInt()))
          .thenReturn(null).thenReturn(null);
    } else {
      final java.sql.Date date = new java.sql.Date(data.getTime());
      final Timestamp timestamp = new Timestamp(data.getTime());
      Mockito.when(resultSet.getDate(Mockito.anyInt()))
          .thenReturn(date)
          .thenReturn(date);
      Mockito.when(resultSet.getTimestamp(Mockito.anyInt()))
          .thenReturn(timestamp)
          .thenReturn(timestamp);
    }
    return resultSet;
  }

  @Test
  public void pattern() {
    assertEquals("yyyy-MM-dd HH:mm:ss", formatter().pattern());
  }

  @Test
  public void conventFn() {
    assertEquals("TO_DATE", formatter().conventFn());
  }

  @Test
  public void oraclePattern() {
    assertEquals("YYYY-MM-DD HH24:MI:SS", formatter().oraclePattern());
  }

  @Test
  public void zone() {
    assertEquals(properties.getTimeZone(), formatter.zone());
  }

  @Test
  public void locale() {
    assertEquals(properties.getLocale(), formatter.locale());
  }

  @Test
  public void type() {
    assertEquals(Types.DATE, formatter.type());
  }

  @Test
  public void formatResultSet() throws SQLException {
    final Date now = new Date();
    assertEquals(formatter.format(now), formatter.format(resultSet(now), 1));
  }

  @Test
  public void nullValue() throws SQLException {
    assertEquals("null", formatter.format(resultSet(null), 1));
  }

  @Test
  public void format() {
    final Date now = new Date();
    final SimpleDateFormat sdf = new SimpleDateFormat(formatter.pattern(), formatter.locale());
    sdf.setTimeZone(TimeZone.getTimeZone(formatter.zone()));
    assertEquals(formatter.conventFn() +
        "('" +
        sdf.format(now) +
        "','" +
        formatter.oraclePattern() +
        "')", formatter.format(now));
  }
}