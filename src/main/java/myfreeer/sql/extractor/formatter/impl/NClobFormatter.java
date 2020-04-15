package myfreeer.sql.extractor.formatter.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@Component
@Slf4j
public class NClobFormatter extends ClobFormatter {
  @Override
  public int type() {
    return Types.NCLOB;
  }

  @Override
  public String format(final ResultSet resultSet, final int col) throws SQLException {
    final NClob clob = resultSet.getNClob(col);
    return getString(clob);
  }

  @Override
  public void format(final Writer writer,
                     final ResultSet resultSet,
                     final int col) throws SQLException, IOException {
    final NClob clob = resultSet.getNClob(col);
    if (clob == null) {
      writer.write("null");
      return;
    }
    format(writer, clob);
  }

  @Override
  protected String stringPrefix() {
    return "(nq'[";
  }

  @Override
  protected String toClobFn() {
    return "TO_NCLOB";
  }
}
