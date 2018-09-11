package de.exlll.databaselib;

import de.exlll.configlib.annotation.Format;
import de.exlll.configlib.configs.yaml.YamlConfiguration;
import de.exlll.configlib.format.FieldNameFormatters;

import java.nio.file.Path;

@Format(FieldNameFormatters.LOWER_UNDERSCORE)
final class DatabaseLibConfiguration extends YamlConfiguration {
    private boolean enableSqlPool = true;

    DatabaseLibConfiguration(Path path) {
        super(path);
    }

    boolean isEnableSqlPool() {
        return enableSqlPool;
    }
}
