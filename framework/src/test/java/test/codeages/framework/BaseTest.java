package test.codeages.framework;

import com.codeages.framework.FrameworkApplication;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FrameworkApplication.class, BaseTest.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EntityScan("test.codeages")
@EnableJpaRepositories("test.codeages")
@ComponentScan(basePackages={"test.codeages"})
@ActiveProfiles("test")
public class BaseTest {

    @Autowired
    private MigrationStrategy migrationStrategy;

    @Autowired
    private RedisConnectionFactory connectionFactory;

    @Before
    public void setUp() {
        migrationStrategy.migrate();
        connectionFactory.getConnection().flushAll();
    }
}
