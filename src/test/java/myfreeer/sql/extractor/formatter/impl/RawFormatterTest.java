package myfreeer.sql.extractor.formatter.impl;

import myfreeer.sql.extractor.util.StringBuilderWriter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.concurrent.ThreadLocalRandom;

import static myfreeer.sql.extractor.formatter.impl.BlobFormatterTest.toHex;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RawFormatterTest {
  private static final RawFormatter FORMATTER = new RawFormatter();

  private ResultSet resultSet(final byte[] data) throws SQLException {
    final ResultSet resultSet = Mockito.mock(ResultSet.class);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    if (data == null) {
      Mockito.when(resultSet.getBytes(Mockito.anyInt()))
          .thenReturn(null).thenReturn(null);
    } else {
      Mockito.when(resultSet.getBytes(Mockito.anyInt()))
          .thenReturn(data).thenReturn(data);
    }
    return resultSet;
  }

  private void assertStreamResultEqualsStringResult(int length) throws SQLException, IOException {
    byte[] bytes = new byte[length];
    ThreadLocalRandom.current().nextBytes(bytes);
    final ResultSet resultSet = resultSet(bytes);
    final StringBuilderWriter writer = new StringBuilderWriter();
    FORMATTER.format(writer, resultSet, 1);
    assertEquals(writer.toString(), FORMATTER.format(resultSet, 1));
  }

  @Test
  public void streamResultEqualsStringResult() throws IOException, SQLException {
    assertStreamResultEqualsStringResult(1);
    assertStreamResultEqualsStringResult(50);
    assertStreamResultEqualsStringResult(100);
    assertStreamResultEqualsStringResult(256);
    assertStreamResultEqualsStringResult(512);
    assertStreamResultEqualsStringResult(1999);
    assertStreamResultEqualsStringResult(2000);

    final ResultSet resultSet = resultSet(null);
    final StringBuilderWriter writer = new StringBuilderWriter();
    FORMATTER.format(writer, resultSet, 1);
    assertEquals(writer.toString(), FORMATTER.format(resultSet, 1));
  }

  @Test
  public void format() throws SQLException {
    for (int i = 0; i < 10; i++) {
      byte[] bytes = new byte[Math.abs(ThreadLocalRandom.current().nextInt() % 1000) + 1000];
      ThreadLocalRandom.current().nextBytes(bytes);
      assertEquals("HEXTORAW('" + toHex(bytes, 0, bytes.length) + "')",
          FORMATTER.format(resultSet(bytes), 1));
    }
  }

  @Test
  public void formatNull() throws SQLException {
    assertEquals("null", FORMATTER.format(resultSet(null), 1));
  }

  @Test
  public void type() {
    assertEquals(Types.VARBINARY, FORMATTER.type());
  }

}