package myfreeer.sql.extractor.formatter.impl;

import lombok.extern.slf4j.Slf4j;
import myfreeer.sql.extractor.properties.ExportProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class TimestampTimeZoneFormatter extends TimestampFormatter {
  private static final Pattern PATTERN = Pattern.compile(
      "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.(\\d+) [+\\-]\\d{1,2}:\\d{2}");

  @Autowired
  public TimestampTimeZoneFormatter(final ExportProperties properties) {
    super(properties);
  }

  @Override
  public int type() {
    return Types.TIMESTAMP_WITH_TIMEZONE;
  }

  @Override
  public String format(final ResultSet resultSet, final int col) throws SQLException {
    final String timestamp = resultSet.getString(col);
    if (timestamp == null) {
      return "null";
    }
    final Matcher matcher = PATTERN.matcher(timestamp);
    if (!matcher.matches()) {
      log.warn("Fail to detect format for timestamp with timezone [{}]," +
          " formatting as normal timestamp...", timestamp);
      return super.format(resultSet, col);
    }
    final String group = matcher.group(1);
    if (StringUtils.isEmpty(group)) {
      log.warn("Fail to detect format for timestamp with timezone [{}]," +
          " formatting as normal timestamp...", timestamp);
      return super.format(resultSet, col);
    }
    return "TO_TIMESTAMP_TZ('" +
        timestamp +
        "', 'YYYY-MM-DD HH24-MI-SS.FF" +
        group.length() +
        " TZH:TZM')";
  }

}
