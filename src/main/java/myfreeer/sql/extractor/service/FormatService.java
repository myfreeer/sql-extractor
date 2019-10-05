package myfreeer.sql.extractor.service;

import lombok.extern.slf4j.Slf4j;
import myfreeer.sql.extractor.formatter.Formatter;
import myfreeer.sql.extractor.formatter.StreamingFormatter;
import myfreeer.sql.extractor.util.OracleTypes;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class FormatService {
  private Map<Integer, Formatter<?>> map;
  private Map<Integer, Integer> alternatives;

  public FormatService(List<Formatter<?>> formatters) {
    map = new ConcurrentHashMap<>();
    formatters.forEach(f -> map.put(f.type(), f));
    alternatives = new ConcurrentHashMap<>();
    alternatives.put(Types.BIGINT, Types.NUMERIC);
    alternatives.put(Types.INTEGER, Types.NUMERIC);
    alternatives.put(Types.BIT, Types.NUMERIC);
    alternatives.put(Types.DECIMAL, Types.NUMERIC);
    alternatives.put(Types.SMALLINT, Types.NUMERIC);
    alternatives.put(Types.TINYINT, Types.NUMERIC);
    alternatives.put(OracleTypes.BINARY_FLOAT, Types.NUMERIC);
    alternatives.put(OracleTypes.BINARY_DOUBLE, Types.NUMERIC);
    alternatives.put(Types.FLOAT, Types.NUMERIC);
    alternatives.put(Types.DOUBLE, Types.NUMERIC);
    alternatives.put(Types.CHAR, Types.VARCHAR);
    alternatives.put(Types.NCHAR, Types.VARCHAR);
    alternatives.put(Types.NVARCHAR, Types.VARCHAR);
    alternatives.put(Types.LONGNVARCHAR, Types.VARCHAR);
    alternatives.put(Types.LONGVARCHAR, Types.VARCHAR);
    alternatives.put(Types.ROWID, Types.VARCHAR);
    alternatives.put(Types.REAL, Types.NUMERIC);
    alternatives.put(Types.TIME, Types.TIMESTAMP);
    alternatives.put(Types.TIME_WITH_TIMEZONE, Types.TIME);
    alternatives.put(Types.TIMESTAMP_WITH_TIMEZONE, Types.TIMESTAMP);
    alternatives.put(OracleTypes.TIMESTAMPTZ, Types.TIMESTAMP_WITH_TIMEZONE);
    alternatives.put(OracleTypes.TIMESTAMPLTZ, Types.TIMESTAMP_WITH_TIMEZONE);
  }

  /**
   * format value to string
   *
   * @param rs     result set with data
   * @param colNum column
   * @param type   jdbc type
   * @return string suitable for sql script
   * @throws SQLException jdbc fails
   * @see Types
   */
  public String format(final ResultSet rs,
                       final int colNum,
                       final int type) throws SQLException {
    Formatter<?> formatter = map.get(type);
    if (formatter == null) {
      final Integer integer = alternatives.get(type);
      if (integer != null) {
        formatter = map.get(integer);
      }
    }
    if (formatter == null) {
      log.info("Formatter not found for type {}", type);
      return "null";
    }
    return formatter.format(rs, colNum);
  }

  /**
   * format value to writer
   *
   * @param writer writer to write value
   * @param rs     result set with data
   * @param colNum column
   * @param type   jdbc type
   * @throws SQLException jdbc fails
   * @see Types
   */
  public void format(final Writer writer,
                     final ResultSet rs,
                     final int colNum,
                     final int type) throws SQLException, IOException {
    Formatter<?> formatter = map.get(type);
    if (formatter == null) {
      final Integer integer = alternatives.get(type);
      if (integer != null) {
        formatter = map.get(integer);
      }
    }
    if (formatter == null) {
      log.info("Formatter not found for type {}", type);
      writer.write("null");
      return;
    }
    if (formatter instanceof StreamingFormatter) {
      ((StreamingFormatter<?>) formatter).format(writer, rs, colNum);
    } else {
      writer.write(formatter.format(rs, colNum));
    }
  }
}
