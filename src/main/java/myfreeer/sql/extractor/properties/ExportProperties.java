package myfreeer.sql.extractor.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
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
