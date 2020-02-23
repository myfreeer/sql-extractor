package myfreeer.sql.extractor.formatter.impl;

import org.junit.Test;

import java.sql.SQLException;
import java.sql.Types;

import static org.junit.Assert.assertEquals;

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
        assertEquals("]' || n']' || n'[", getFormatter().escapingDelimiter());
    }

    @Test
    @Override
    public void escapingSuffix() {
        assertEquals("]' || n']')", getFormatter().escapingSuffix());
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
        assertEquals("(nq'[" + getRandomStr() + "]' || n']' || n'[" + getRandomStr() + "]')",
                getFormatter().format(resultSet(getRandomStr() + ']' + getRandomStr()), 1));
    }

    @Test
    @Override
    public void escapingStringAtEnd() throws SQLException {
        assertEquals("(nq'[" + getRandomStr() + "]' || n']')",
                getFormatter().format(resultSet(getRandomStr() + ']'), 1));
    }

}