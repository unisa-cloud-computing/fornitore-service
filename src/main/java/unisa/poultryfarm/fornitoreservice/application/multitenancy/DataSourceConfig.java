package unisa.poultryfarm.fornitoreservice.application.multitenancy;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Value("${app.datasource.server-name}")
    private String serverName;

    // ── EntityManagerFactory ────────────────────────────────────────────────
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("catalogDataSource") DataSource catalogDataSource,
            MultiTenantConnectionProvider<String> connectionProvider,
            CurrentTenantIdentifierResolver<String> tenantResolver
    ) {

        Map<String, Object> props = new HashMap<>();

        props.put("hibernate.multiTenancy", "SCHEMA");
        props.put("hibernate.multi_tenant_connection_provider", connectionProvider);
        props.put("hibernate.tenant_identifier_resolver", tenantResolver);

        return builder
                .dataSource(catalogDataSource)
                .packages("unisa.poultryfarm.fornitoreservice")
                .properties(props)
                .build();
    }

    // ── Catalog DataSource ────────────────────────────────────────────────────

    /**
     * DataSource del Catalog DB (db-catalog).
     * Usato solo da CatalogRepository per risolvere tenantId → dbName.
     */
    @Bean(name = "catalogDataSource")
    public DataSource catalogDataSource() {
        return buildDataSource("db-catalog");
    }

    @Bean(name = "catalogJdbcTemplate")
    public JdbcTemplate catalogJdbcTemplate(@Qualifier("catalogDataSource") DataSource catalogDataSource) {
        return new JdbcTemplate(catalogDataSource);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private DataSource buildDataSource(String databaseName) {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setServerName(serverName);
        ds.setDatabaseName(databaseName);
        ds.setEncrypt("true");
        ds.setTrustServerCertificate(false);
        ds.setAuthentication("ActiveDirectoryManagedIdentity");
        return ds;
    }
}
