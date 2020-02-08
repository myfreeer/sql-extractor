package myfreeer.sql.extractor.formatter.impl;

import lombok.extern.slf4j.Slf4j;
import myfreeer.sql.extractor.properties.ExportProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class TimestampTimeZoneFormatter extends TimestampFormatter {
  private final DateTimeFormatter timestampFormatter;

  @Autowired
  public TimestampTimeZoneFormatter(final ExportProperties properties) {
    super(properties);
    timestampFormatter = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd HH:mm:ss.SSSSSS xxx", locale());
  }

  @Override
  public int type() {
    return Types.TIMESTAMP_WITH_TIMEZONE;
  }

  @Override
  public String format(final ResultSet resultSet, final int col) throws SQLException {
    try {
      final OffsetDateTime offsetDateTime = resultSet.getObject(col, OffsetDateTime.class);
      if (offsetDateTime == null) {
        return "null";
      }
      return format(offsetDateTime);
    } catch (SQLException ex) {
      log.warn("Fail to get OffsetDateTime from col {}, falling back with timestamp", col, ex);
      return super.format(resultSet, col);
    }
  }

  String format(final OffsetDateTime offsetDateTime) {
    return "TO_TIMESTAMP_TZ('" +
            timestampFormatter.format(offsetDateTime) +
            "', 'YYYY-MM-DD HH24-MI-SS.FF6 TZH:TZM')";
  }

}
