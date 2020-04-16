package test.codeages.framework;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Profile;

@TestComponent
@Profile("test")
public class MigrationStrategy implements FlywayMigrationStrategy {
    private Flyway flyway;
    @Override
    public void migrate(Flyway flyway) {
        this.flyway = flyway;
        flyway.clean();
        flyway.migrate();
    }

    public void migrate(){
        flyway.clean();
        flyway.migrate();
    }
}
