package myfreeer.sql.extractor.properties;

import lombok.Data;
import lombok.experimental.Accessors;
import myfreeer.sql.extractor.formatter.impl.DateFormatter;
import myfreeer.sql.extractor.formatter.impl.TimestampFormatter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
@Configuration
@ConfigurationProperties("export")
@EnableConfigurationProperties
public class ExportProperties {
  /**
   * Export type
   */
  private ExportType type = ExportType.FULL;
  /**
   * Export file path
   */
  private String filePath = "export_" + System.currentTimeMillis() + ".sql";
  /**
   * Tables to export, effective if type is table
   */
  private List<String> tables = Collections.emptyList();

  /**
   * Tables to exclude, effective if type is table or full
   */
  private Set<String> excludeTables = Collections.emptySet();

  /**
   * Sql select statements for exporting, effective if type is sql
   */
  private Map<String, String> sql = new LinkedHashMap<>();

  /**
   * Time zone for {@link DateFormatter} and {@link TimestampFormatter}
   * <br/>
   * Example: UTC, GMT+8
   */
  private ZoneId timeZone = ZoneId.systemDefault();

  /**
   * Locale for {@link DateFormatter} and {@link TimestampFormatter}
   * <br/>
   * Example: en-US, zh-CN
   */
  private Locale locale = Locale.getDefault();

  /**
   * Treat table or column names case sensitively or not
   */
  private boolean caseSensitive = true;

  /**
   * Sql statement to get all table name case-sensitively from db,
   * effective if type is full
   */
  private String sqlSelectTableNames =
          "select TABLE_NAME from USER_TABLES order by TABLE_NAME";

  /**
   * Sql statement to get all table name in lower case from db,
   * effective if type is full
   */
  private String sqlSelectLowerCaseTableNames =
          "select lower(TABLE_NAME) from USER_TABLES order by TABLE_NAME";

  public List<String> tables() {
    return caseSensitive ? tables :
            tables.stream().map(String::toLowerCase).collect(Collectors.toList());
  }

  public Set<String> excludeTables() {
    return caseSensitive ? excludeTables :
            excludeTables.stream().map(String::toLowerCase)
                    .collect(Collectors.toSet());
  }

  public String sqlSelectTableNames() {
    return caseSensitive ? sqlSelectTableNames : sqlSelectLowerCaseTableNames;
  }

  public enum ExportType {
    /**
     * Export specified tables in {@code tables}, excluding {@code excludeTables}
     */
    TABLE,
    /**
     * Export all tables in current schema, excluding {@code excludeTables}
     */
    FULL,
    /**
     * Export tables via custom sql select statement
     */
    SQL
  }
}
