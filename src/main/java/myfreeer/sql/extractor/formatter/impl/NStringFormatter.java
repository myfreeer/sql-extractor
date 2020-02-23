package myfreeer.sql.extractor.formatter.impl;

import org.springframework.stereotype.Component;

import java.sql.Types;

@Component
public class NStringFormatter extends StringFormatter {
    @Override
    public int type() {
        return Types.NVARCHAR;
    }

    @Override
    protected String prefix() {
        return "(nq'[";
    }

    @Override
    protected String escapingDelimiter() {
        return "]' || n']' || n'[";
    }

    @Override
    protected String suffix() {
        return "]')";
    }

    @Override
    protected String escapingSuffix() {
        return "]' || n']')";
    }
}
