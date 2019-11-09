package myfreeer.sql.extractor.formatter.impl;

import myfreeer.sql.extractor.util.StringBuilderWriter;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;

public class StringFormatterTest {

  private static final Random RANDOM = ThreadLocalRandom.current();
  private static final String RANDOM_STR;
  private static final StringFormatter FORMATTER = new StringFormatter();

  static {
    final byte[] bytes = new byte[RANDOM.nextInt(256) + 256];
    RANDOM.nextBytes(bytes);
    RANDOM_STR = Base64.getEncoder().encodeToString(bytes);
  }

  private ResultSet resultSet(final String data) throws SQLException {
    final ResultSet resultSet = Mockito.mock(ResultSet.class);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    if (data == null) {
      Mockito.when(resultSet.getString(Mockito.anyInt()))
          .thenReturn(null).thenReturn(null);
    } else {
      Mockito.when(resultSet.getString(Mockito.anyInt()))
          .thenReturn(data).thenReturn(data);
    }
    return resultSet;
  }

  @Test
  public void streamResultEqualsStringResult() throws IOException, SQLException {
    assertStreamResultEqualsStringResult(null);
    assertStreamResultEqualsStringResult("1");
    assertStreamResultEqualsStringResult("111");
    assertStreamResultEqualsStringResult(RANDOM_STR);
    assertStreamResultEqualsStringResult(RANDOM_STR + ']' + RANDOM_STR);
    assertStreamResultEqualsStringResult(RANDOM_STR + ']');
  }

  private void assertStreamResultEqualsStringResult(String string) throws SQLException, IOException {
    final ResultSet resultSet = resultSet(string);
    final StringBuilderWriter writer = new StringBuilderWriter();
    FORMATTER.format(writer, resultSet, 1);
    assertEquals(writer.toString(), FORMATTER.format(resultSet, 1));
  }

  @Test
  public void type() {
    assertEquals(FORMATTER.type(), Types.VARCHAR);
  }

  @Test
  public void nullValue() throws SQLException {
    assertEquals("null", FORMATTER.format(resultSet(null), 1));
  }

  @Test
  public void simpleString() throws SQLException {
    assertEquals("(q'[" + RANDOM_STR + "]')",
        FORMATTER.format(resultSet(RANDOM_STR), 1));
  }

  @Test
  public void escapingString() throws SQLException {
    assertEquals("(q'[" + RANDOM_STR + "]' || ']' || q'[" + RANDOM_STR + "]')",
        FORMATTER.format(resultSet(RANDOM_STR + ']' + RANDOM_STR), 1));
  }

  @Test
  public void escapingStringAtEnd() throws SQLException {
    assertEquals("(q'[" + RANDOM_STR + "]' || ']')",
        FORMATTER.format(resultSet(RANDOM_STR + ']'), 1));
  }
}