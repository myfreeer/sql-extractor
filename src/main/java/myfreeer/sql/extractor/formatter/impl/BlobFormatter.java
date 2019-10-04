package myfreeer.sql.extractor.formatter.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myfreeer.sql.extractor.formatter.StreamingFormatter;
import myfreeer.sql.extractor.util.StringBuilderWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@RequiredArgsConstructor
@Getter
@Slf4j
public class BlobFormatter implements StreamingFormatter<InputStream> {
  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
  private final String fnName;

  @Override
  public int type() {
    return Types.BLOB;
  }

  @Override
  public String format(final ResultSet resultSet, final int col) throws SQLException {
    final Blob blob = resultSet.getBlob(col);
    if (blob == null) {
      return "null";
    }
    final StringBuilderWriter writer =
        new StringBuilderWriter((int) blob.length() << 1);
    try {
      format(writer, blob);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return writer.toString();
  }

  public void format(final Writer sb,
                     final Blob blob) throws SQLException, IOException {
    try (final InputStream inputStream = blob.getBinaryStream()) {
      sb.append(fnName).append('(');
      byte[] buffer = new byte[512];
      int i;
      int b;
      boolean firstAppend = true;
      while ((i = inputStream.read(buffer)) != -1) {
        if (firstAppend) {
          firstAppend = false;
        } else {
          sb.write("') || ");
        }
        sb.write("TO_CLOB('");
        for (int p = 0; p < i; p++) {
          b = buffer[p] & 0xFF;
          sb.write(HEX_ARRAY[b >>> 4]);
          sb.write(HEX_ARRAY[b & 0x0F]);
        }
      }
      sb.write("'))");
    }
  }

  @Override
  public void format(final Writer writer,
                     final ResultSet resultSet,
                     final int col) throws SQLException, IOException {
    final Blob blob = resultSet.getBlob(col);
    if (blob == null) {
      writer.write("null");
      return;
    }
    format(writer, blob);
  }
}
