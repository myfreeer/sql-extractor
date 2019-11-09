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

  @Override
  public String format(final ResultSet resultSet, final int col) throws SQLException {
    final String string = resultSet.getString(col);
    if (string == null) {
      return "null";
    }
    final int length = string.length();
    final StringBuilder sb = new StringBuilder(length + 10);
    sb.append("(q'[");
    char c;
    for (int i = 0; i < length; i++) {
      if ((c = string.charAt(i)) == ']') {
        if (i == length - 1) {
          sb.append("]' || ']')");
          return sb.toString();
        }
        sb.append("]' || ']' || q'[");
        continue;
      }
      sb.append(c);
    }
    sb.append("]')");
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
    writer.write("(q'[");
    char c;
    for (int i = 0; i < length; i++) {
      if ((c = string.charAt(i)) == ']') {
        if (i == length - 1) {
          writer.write("]' || ']')");
          return;
        }
        writer.write("]' || ']' || q'[");
        continue;
      }
      writer.write(c);
    }
    writer.write("]')");
  }
}
