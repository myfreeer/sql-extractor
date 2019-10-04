package myfreeer.sql.extractor.formatter.impl;

import myfreeer.sql.extractor.formatter.Formatter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@Component
public class NumberFormatter implements Formatter<Number> {
  @Override
  public int type() {
    return Types.NUMERIC;
  }

  @Override
  public String format(final ResultSet resultSet, final int col) throws SQLException {
    final BigDecimal bigDecimal = resultSet.getBigDecimal(col);
    if (bigDecimal == null) {
      return "null";
    }
    return bigDecimal.toString();
  }

}
