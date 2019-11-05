package myfreeer.sql.extractor.formatter.impl;

import myfreeer.sql.extractor.formatter.StreamingFormatter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;

@Component
public class RawFormatter implements StreamingFormatter<byte[]> {
  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
  private static final char[] FUNC = "HEXTORAW('".toCharArray();

  @Override
  public void format(final Writer writer,
                     final ResultSet resultSet,
                     final int col) throws SQLException, IOException {
    final byte[] bytes = resultSet.getBytes(col);
    if (bytes == null) {
      writer.write("null");
      return;
    }
    writer.write(FUNC);
    for (int i = 0, b; i < bytes.length; i++) {
      b = bytes[i] & 0xFF;
      writer.write(HEX_ARRAY[b >>> 4]);
      writer.write(HEX_ARRAY[b & 0x0F]);
    }
    writer.write('\'');
    writer.write(')');
  }

  @Override
  public int type() {
    return Types.VARBINARY;
  }

  @Override
  public String format(final ResultSet resultSet, final int col) throws SQLException {
    final byte[] bytes = resultSet.getBytes(col);
    if (bytes == null) {
      return "null";
    }
    final char[] buffer = Arrays.copyOf(FUNC,
        (bytes.length << 1) + FUNC.length + 2);
    for (int i = 0, j = FUNC.length, b; i < bytes.length; i++) {
      b = bytes[i] & 0xFF;
      buffer[j++] = HEX_ARRAY[b >>> 4];
      buffer[j++] = HEX_ARRAY[b & 0x0F];
    }
    buffer[buffer.length - 2] = '\'';
    buffer[buffer.length - 1] = ')';
    return new String(buffer);
  }
}
