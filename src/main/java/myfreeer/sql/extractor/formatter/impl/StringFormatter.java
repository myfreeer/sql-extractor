package myfreeer.sql.extractor.formatter.impl;

import myfreeer.sql.extractor.formatter.StreamingFormatter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@Component
public class StringFormatter implements StreamingFormatter<String> {
  @Override
  public int type() {
    return Types.VARCHAR;
  }

  protected String prefix() {
    return "(q'[";
  }

  protected String escapingDelimiter() {
    return "]' || ']' || q'[";
  }

  protected String suffix() {
    return "]')";
  }

  protected String escapingSuffix() {
    return "]' || ']')";
  }

  @Override
  public String format(final ResultSet resultSet, final int col) throws SQLException {
    final String string = resultSet.getString(col);
    if (string == null) {
      return "null";
    }
    final int length = string.length();
    final StringBuilder sb = new StringBuilder(length + 10);
    sb.append(prefix());
    char c;
    for (int i = 0; i < length; i++) {
      if ((c = string.charAt(i)) == ']') {
        if (i == length - 1) {
          sb.append(escapingSuffix());
          return sb.toString();
        }
        sb.append(escapingDelimiter());
        continue;
      }
      sb.append(c);
    }
    sb.append(suffix());
    return sb.toString();
  }

  @Override
  public void format(final Writer writer,
                     final ResultSet resultSet,
                     final int col) throws SQLException, IOException {
    final String string = resultSet.getString(col);
    if (string == null) {
      writer.write("null");
      return;
    }
    final int length = string.length();
    writer.write(prefix());
    char c;
    for (int i = 0; i < length; i++) {
      if ((c = string.charAt(i)) == ']') {
        if (i == length - 1) {
          writer.write(escapingSuffix());
          return;
        }
        writer.write(escapingDelimiter());
        continue;
      }
      writer.write(c);
    }
    writer.write(suffix());
  }
}
