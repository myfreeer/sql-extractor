package myfreeer.sql.extractor.formatter.impl;

import myfreeer.sql.extractor.formatter.Formatter;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@Component
public class StringFormatter implements Formatter<String> {
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

}
