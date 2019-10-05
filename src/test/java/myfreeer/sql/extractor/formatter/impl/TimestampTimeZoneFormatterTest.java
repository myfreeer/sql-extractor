package myfreeer.sql.extractor.formatter.impl;

import org.junit.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class TimestampTimeZoneFormatterTest extends TimestampFormatterTest {
  private final TimestampTimeZoneFormatter formatter = new TimestampTimeZoneFormatter(properties);

  protected ResultSet resultSet(final Date data, final String string) throws SQLException {
    final ResultSet resultSet = Mockito.mock(ResultSet.class);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    Mockito.when(resultSet.getString(Mockito.anyInt()))
        .thenReturn(string).thenReturn(string);
    if (data == null) {
      Mockito.when(resultSet.getDate(Mockito.anyInt()))
          .thenReturn(null).thenReturn(null);
      Mockito.when(resultSet.getTimestamp(Mockito.anyInt()))
          .thenReturn(null).thenReturn(null);
    } else {
      final java.sql.Date date = new java.sql.Date(data.getTime());
      final Timestamp timestamp = new Timestamp(data.getTime());
      Mockito.when(resultSet.getDate(Mockito.anyInt()))
          .thenReturn(date).thenReturn(date);
      Mockito.when(resultSet.getTimestamp(Mockito.anyInt()))
          .thenReturn(timestamp).thenReturn(timestamp);
    }
    return resultSet;
  }

  private SimpleDateFormat sdf(final String pattern) {
    final SimpleDateFormat sdf = new SimpleDateFormat(pattern, formatter().locale());
    sdf.setTimeZone(TimeZone.getTimeZone(formatter().zone()));
    return sdf;
  }

  @Override
  protected TimestampTimeZoneFormatter formatter() {
    return formatter;
  }

  @Override
  public void type() {
    assertEquals(formatter().type(), Types.TIMESTAMP_WITH_TIMEZONE);
  }

  private String repeat(final int count) {
    final char[] chars = new char[count];
    for (int i = 0; i < count; i++) {
      chars[i] = 'S';
    }
    return new String(chars);
  }

  private void assertValidValue(final int f, final String timeZone) throws SQLException {
    final Date now = new Date();
    final String s = sdf("yyyy-MM-dd HH:mm:ss." + repeat(f)).format(now) + " " + timeZone;
    assertEquals(formatter().format(resultSet(now, s), 1), "TO_TIMESTAMP_TZ('" +
        s +
        "', 'YYYY-MM-DD HH24-MI-SS.FF" +
        f +
        " TZH:TZM')");
  }

  @Override
  public void nullValue() throws SQLException {
    assertEquals("null", formatter().format(resultSet(null), 1));
  }

  @Test
  public void formatValidValue() throws SQLException {
    for (int i = 3; i < 10; i++) {
      assertValidValue(i, "+05:00");
      assertValidValue(i, "-10:00");
      assertValidValue(i, "00:00");
    }
  }

  @Test
  public void formatInvalidValue() throws SQLException {
    final Date now = new Date();
    final String s = sdf("yyyy-MM-dd HH:mm:ss").format(now) + " 00:00";
    assertEquals(formatter().format(resultSet(now, s), 1), formatter().format(now));
  }
}
