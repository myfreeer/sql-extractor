package myfreeer.sql.extractor.formatter.impl;

import org.junit.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.zone.ZoneRulesProvider;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class TimestampTimeZoneFormatterTest extends TimestampFormatterTest {
  private final TimestampTimeZoneFormatter formatter = new TimestampTimeZoneFormatter(properties);
  private final ZoneId defaultZone = ZoneId.systemDefault();

  protected ResultSet resultSet(final Date data) throws SQLException {
    if (data == null) {
      return resultSet((OffsetDateTime) null);
    }
    final Instant instant = data.toInstant();
    return resultSet(instant.atOffset(defaultZone.getRules().getOffset(instant)));
  }

  protected ResultSet fallbackResultSet(final Date data) throws SQLException {
    final ResultSet resultSet = super.resultSet(data);
    Mockito.when(resultSet.getObject(Mockito.anyInt(), Mockito.eq(OffsetDateTime.class)))
            .thenThrow(SQLException.class).thenThrow(SQLException.class);
    return resultSet;
  }

  @SuppressWarnings("unchecked")
  protected ResultSet resultSet(final OffsetDateTime offsetDateTime) throws SQLException {
    final ResultSet resultSet;
    if (offsetDateTime == null) {
      resultSet = super.resultSet(null);
      Mockito.when(resultSet.getObject(Mockito.anyInt(), Mockito.any(Class.class)))
              .thenReturn(null).thenReturn(null);
    } else {
      final Date date = Date.from(offsetDateTime.toInstant());
      resultSet = super.resultSet(date);
      Mockito.when(resultSet.getObject(Mockito.anyInt(), Mockito.eq(OffsetDateTime.class)))
              .thenReturn(offsetDateTime).thenReturn(offsetDateTime);
    }
    return resultSet;
  }

  @Override
  protected TimestampTimeZoneFormatter formatter() {
    return formatter;
  }

  @Override
  public void type() {
    assertEquals(formatter().type(), Types.TIMESTAMP_WITH_TIMEZONE);
  }

  @Override
  public void nullValue() throws SQLException {
    assertEquals("null", formatter().format(resultSet((OffsetDateTime) null), 1));
  }

  @Override
  public void formatResultSet() throws SQLException {
    final OffsetDateTime now = OffsetDateTime.now(defaultZone);
    assertEquals(formatter.format(now), formatter.format(resultSet(now), 1));
  }

  @Test
  public void formatConstant() throws SQLException {
    OffsetDateTime offsetDateTime = OffsetDateTime.of(2012,12,21,
            12,34,56, 778899000,
            ZoneOffset.ofHoursMinutes(7, 5));
    assertEquals("TO_TIMESTAMP_TZ('2012-12-21 12:34:56.778899 +07:05', 'YYYY-MM-DD HH24-MI-SS.FF6 TZH:TZM')",
            formatter.format(resultSet(offsetDateTime), 1));
    offsetDateTime = OffsetDateTime.of(2012, 12, 21,
            12, 34, 56, 778899000,
            ZoneOffset.ofHoursMinutes(-11, -25));
    assertEquals("TO_TIMESTAMP_TZ('2012-12-21 12:34:56.778899 -11:25', 'YYYY-MM-DD HH24-MI-SS.FF6 TZH:TZM')",
            formatter.format(resultSet(offsetDateTime), 1));
    offsetDateTime = OffsetDateTime.of(2012, 12, 21,
            12, 34, 56, 778899000,
            ZoneOffset.ofHoursMinutes(0, 0));
    assertEquals("TO_TIMESTAMP_TZ('2012-12-21 12:34:56.778899 +00:00', 'YYYY-MM-DD HH24-MI-SS.FF6 TZH:TZM')",
            formatter.format(resultSet(offsetDateTime), 1));
  }

  @Test
  public void formatResultSetAtZone() throws SQLException {
    ZoneId zoneId;
    OffsetDateTime now;
    for (String s : ZoneRulesProvider.getAvailableZoneIds()) {
      zoneId = ZoneId.of(s);
      now = OffsetDateTime.now(zoneId);
      assertEquals(formatter.format(now), formatter.format(resultSet(now), 1));
    }
  }

  @Test
  public void fallbackFormat() throws SQLException {
    final ResultSet fallback = fallbackResultSet(new Date());
    assertEquals(formatter.format(fallback, 1), super.formatter().format(fallback, 1));
  }
}
