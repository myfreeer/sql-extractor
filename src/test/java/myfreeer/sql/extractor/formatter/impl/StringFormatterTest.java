package myfreeer.sql.extractor.formatter.impl;

import myfreeer.sql.extractor.util.StringBuilderWriter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringFormatterTest {

  private static final String RANDOM_STR;
  private static final StringFormatter FORMATTER = new StringFormatter();
  protected static final String JSON_EXAMPLE_DATA =
          // from https://opensource.adobe.com/Spry/samples/data_region/JSONDataSetSample.html
          "{\n" +
                  "\t\"id\": \"0001\",\n" +
                  "\t\"type\": \"donut\",\n" +
                  "\t\"name\": \"Cake\",\n" +
                  "\t\"ppu\": 0.55,\n" +
                  "\t\"batters\":\n" +
                  "\t\t{\n" +
                  "\t\t\t\"batter\":\n" +
                  "\t\t\t\t[\n" +
                  "\t\t\t\t\t{ \"id\": \"1001\", \"type\": \"Regular\" },\n" +
                  "\t\t\t\t\t{ \"id\": \"1002\", \"type\": \"Chocolate\" },\n" +
                  "\t\t\t\t\t{ \"id\": \"1003\", \"type\": \"Blueberry\" },\n" +
                  "\t\t\t\t\t{ \"id\": \"1004\", \"type\": \"Devil's Food\" }\n" +
                  "\t\t\t\t]\n" +
                  "\t\t},\n" +
                  "\t\"topping\":\n" +
                  "\t\t[\n" +
                  "\t\t\t{ \"id\": \"5001\", \"type\": \"None\" },\n" +
                  "\t\t\t{ \"id\": \"5002\", \"type\": \"Glazed\" },\n" +
                  "\t\t\t{ \"id\": \"5005\", \"type\": \"Sugar\" },\n" +
                  "\t\t\t{ \"id\": \"5007\", \"type\": \"Powdered Sugar\" },\n" +
                  "\t\t\t{ \"id\": \"5006\", \"type\": \"Chocolate with Sprinkles\" },\n" +
                  "\t\t\t{ \"id\": \"5003\", \"type\": \"Chocolate\" },\n" +
                  "\t\t\t{ \"id\": \"5004\", \"type\": \"Maple\" }\n" +
                  "\t\t]\n" +
                  "}";

  static {
    final Random random = ThreadLocalRandom.current();
    final byte[] bytes = new byte[random.nextInt(256) + 256];
    random.nextBytes(bytes);
    RANDOM_STR = Base64.getEncoder().encodeToString(bytes);
  }

  protected static String getRandomStr() {
    return RANDOM_STR;
  }

  protected ResultSet resultSet(final String data) throws SQLException {
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
    getFormatter().format(writer, resultSet, 1);
    assertEquals(writer.toString(), getFormatter().format(resultSet, 1));
  }

  @Test
  public void type() {
    assertEquals(Types.VARCHAR, getFormatter().type());
  }

  @Test
  public void nullValue() throws SQLException {
    assertEquals("null", getFormatter().format(resultSet(null), 1));
  }

  @Test
  public void simpleString() throws SQLException {
    assertEquals("(q'[" + RANDOM_STR + "]')",
        getFormatter().format(resultSet(RANDOM_STR), 1));
  }

  @Test
  public void escapingString() throws SQLException {
    assertEquals("(q'[" + RANDOM_STR + "]' || ']' || q'[" + RANDOM_STR + "]')",
            getFormatter().format(resultSet(RANDOM_STR + ']' + RANDOM_STR), 1));
  }

  @Test
  public void escapingStringAtEnd() throws SQLException {
    assertEquals("(q'[" + RANDOM_STR + "]' || ']')",
            getFormatter().format(resultSet(RANDOM_STR + ']'), 1));
  }

  @Test
  public void prefix() {
    assertEquals("(q'[", getFormatter().prefix());
  }

  protected StringFormatter getFormatter() {
    return FORMATTER;
  }

  @Test
  public void escapingDelimiter() {
    assertEquals("]' || ']' || q'[", getFormatter().escapingDelimiter());
  }

  @Test
  public void suffix() {
    assertEquals("]')", getFormatter().suffix());
  }

  @Test
  public void escapingSuffix() {
    assertEquals("]' || ']')", getFormatter().escapingSuffix());
  }

  @Test
  public void formatJson() throws SQLException {
    assertEquals(
                    "(q'[{\n" +
                    "\t\"id\": \"0001\",\n" +
                    "\t\"type\": \"donut\",\n" +
                    "\t\"name\": \"Cake\",\n" +
                    "\t\"ppu\": 0.55,\n" +
                    "\t\"batters\":\n" +
                    "\t\t{\n" +
                    "\t\t\t\"batter\":\n" +
                    "\t\t\t\t[\n" +
                    "\t\t\t\t\t{ \"id\": \"1001\", \"type\": \"Regular\" },\n" +
                    "\t\t\t\t\t{ \"id\": \"1002\", \"type\": \"Chocolate\" },\n" +
                    "\t\t\t\t\t{ \"id\": \"1003\", \"type\": \"Blueberry\" },\n" +
                    "\t\t\t\t\t{ \"id\": \"1004\", \"type\": \"Devil's Food\" }\n" +
                    "\t\t\t\t]' || ']' || q'[\n" +
                    "\t\t},\n" +
                    "\t\"topping\":\n" +
                    "\t\t[\n" +
                    "\t\t\t{ \"id\": \"5001\", \"type\": \"None\" },\n" +
                    "\t\t\t{ \"id\": \"5002\", \"type\": \"Glazed\" },\n" +
                    "\t\t\t{ \"id\": \"5005\", \"type\": \"Sugar\" },\n" +
                    "\t\t\t{ \"id\": \"5007\", \"type\": \"Powdered Sugar\" },\n" +
                    "\t\t\t{ \"id\": \"5006\", \"type\": \"Chocolate with Sprinkles\" },\n" +
                    "\t\t\t{ \"id\": \"5003\", \"type\": \"Chocolate\" },\n" +
                    "\t\t\t{ \"id\": \"5004\", \"type\": \"Maple\" }\n" +
                    "\t\t]' || ']' || q'[\n" +
                    "}]')",
            getFormatter().format(resultSet(JSON_EXAMPLE_DATA), 1));
  }
}
