package myfreeer.sql.extractor.formatter.impl;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Types;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NStringFormatterTest extends StringFormatterTest {
    private static final NStringFormatter FORMATTER = new NStringFormatter();

    @Test
    @Override
    public void prefix() {
        assertEquals("(nq'[", getFormatter().prefix());
    }

    @Test
    @Override
    public void type() {
        assertEquals(Types.NVARCHAR, getFormatter().type());
    }

    @Override
    protected StringFormatter getFormatter() {
        return FORMATTER;
    }

    @Test
    @Override
    public void escapingDelimiter() {
        assertEquals("]' || n']' || nq'[", getFormatter().escapingDelimiter());
    }

    @Test
    @Override
    public void escapingSuffix() {
        assertEquals("]' || n']')", getFormatter().escapingSuffix());
    }

    @Override
    public void formatJson() throws SQLException {
        assertEquals(
                "(nq'[{\n" +
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
                "\t\t\t\t]' || n']' || nq'[\n" +
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
                "\t\t]' || n']' || nq'[\n" +
                "}]')",
                getFormatter().format(resultSet(JSON_EXAMPLE_DATA), 1));
    }

    @Test
    @Override
    public void simpleString() throws SQLException {
        assertEquals("(nq'[" + getRandomStr() + "]')",
                getFormatter().format(resultSet(getRandomStr()), 1));
    }

    @Test
    @Override
    public void escapingString() throws SQLException {
        assertEquals("(nq'[" + getRandomStr() + "]' || n']' || nq'[" + getRandomStr() + "]')",
                getFormatter().format(resultSet(getRandomStr() + ']' + getRandomStr()), 1));
    }

    @Test
    @Override
    public void escapingStringAtEnd() throws SQLException {
        assertEquals("(nq'[" + getRandomStr() + "]' || n']')",
                getFormatter().format(resultSet(getRandomStr() + ']'), 1));
    }

}
