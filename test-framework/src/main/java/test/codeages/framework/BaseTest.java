package test.codeages.framework;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BaseTest.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EntityScan({"test.codeages", "com.codeages"})
@EnableJpaRepositories({"test.codeages", "com.codeages"})
@ComponentScan(basePackages={"test.codeages","com.codeages" })
@EnableJpaAuditing
@EnableCaching
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
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
