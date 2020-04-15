package myfreeer.sql.extractor.formatter.impl;

import myfreeer.sql.extractor.formatter.Formatter;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@Component
public class FloatFormatter implements Formatter<Number> {
  @Override
  public int type() {
    return Types.FLOAT;
  }

  @Override
  public String format(final ResultSet resultSet, final int col) throws SQLException {
    final Float num = resultSet.getObject(col, Float.class);
    if (num == null) {
      return "null";
    }
    return num.toString();
  }

}
