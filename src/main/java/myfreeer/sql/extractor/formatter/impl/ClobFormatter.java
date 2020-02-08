package myfreeer.sql.extractor.formatter.impl;

import lombok.extern.slf4j.Slf4j;
import myfreeer.sql.extractor.formatter.StreamingFormatter;
import myfreeer.sql.extractor.util.StringBuilderWriter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.sql.*;

@Slf4j
@Component
public class ClobFormatter implements StreamingFormatter<Clob> {
  @Override
  public int type() {
    return Types.CLOB;
  }

  @Override
  public String format(final ResultSet resultSet, final int col) throws SQLException {
    final Clob clob = resultSet.getClob(col);
    return getString(clob);
  }

  protected String getString(final Clob clob) throws SQLException {
    if (clob == null) {
      return "null";
    }
    final StringBuilderWriter writer =
        new StringBuilderWriter((int) clob.length() + 50);
    try {
      format(writer, clob);
    } catch (IOException e) {
      throw new SQLRecoverableException("Read clob fail", e);
    }
    return writer.toString();
  }

  protected String toClobFn() {
    return "TO_CLOB";
  }

  protected void format(final Writer writer, final Clob clob)
      throws SQLException, IOException {
    final Reader r = clob.getCharacterStream();
    writer.append(toClobFn()).append("(q'[");
    int b, lastLen = 0;
    while (-1 != (b = r.read())) {
      if (b == ']') {
        lastLen = 0;
        writer.append("]')\n || ")
            .append(toClobFn()).append("(']') || ")
            .append(toClobFn()).append("(q'[");
        continue;
      }
      if (++lastLen == 2000) {
        writer.append("]')\n || ").append(toClobFn()).append("(q'[");
        lastLen = 0;
      }
      writer.append((char) b);
    }
    writer.append("]')");

  }

  @Override
  public void format(final Writer writer,
                     final ResultSet resultSet,
                     final int col) throws SQLException, IOException {
    final Clob clob = resultSet.getClob(col);
    if (clob == null) {
      writer.write("null");
      return;
    }
    format(writer, clob);
  }
}
