package myfreeer.sql.extractor.properties;

import lombok.Data;
import lombok.experimental.Accessors;
import myfreeer.sql.extractor.formatter.impl.DateFormatter;
import myfreeer.sql.extractor.formatter.impl.TimestampFormatter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
@Configuration
@ConfigurationProperties("export")
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

  public List<String> lowerCaseTables() {
    return tables.stream().map(String::toLowerCase).collect(Collectors.toList());
  }

  public Set<String> lowerCaseExcludeTables() {
    return excludeTables.stream().map(String::toLowerCase).collect(Collectors.toSet());
  }

  public enum ExportType {
    /**
     * Export specified tables in {@code tables}, excluding {@code excludeTables}
     */
    TABLE,
    /**
     * Export all tables in current schema, excluding {@code excludeTables}
     */
    FULL
  }
}
