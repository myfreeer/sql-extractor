package myfreeer.sql.extractor.formatter;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Formatter<T> {
  /**
   * SQL 类型
   *
   * @return {@link java.sql.Types}
   */
  int type();

  /**
   * 格式化值到 SQL 字符串
   *
   * @param resultSet resultSet，里面必须有内容
   * @param col       列
   * @return SQL 字符串
   * @throws SQLException ResultSet 抛出异常
   */
  String format(ResultSet resultSet, int col) throws SQLException;
}
