package com.saravyasystems.filminex;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FilminexApiApplicationTests {

    @Autowired
    private JdbcClient jdbcClient;

    @Test
    void contextLoadsWithMigratedDatabase() {
        Long appliedMigrations = jdbcClient.sql("""
                        select count(*)
                        from filminex.flyway_schema_history
                        where version = '1'
                          and success = true
                        """)
                .query(Long.class)
                .single();

        assertThat(appliedMigrations).isEqualTo(1L);
        assertThat(tableExists("workspace")).isTrue();
        assertThat(tableExists("project")).isTrue();
    }

    private boolean tableExists(String tableName) {
        return jdbcClient.sql("""
                        select exists (
                            select 1
                            from information_schema.tables
                            where table_schema = 'filminex'
                              and table_name = :tableName
                        )
                        """)
                .param("tableName", tableName)
                .query(Boolean.class)
                .single();
    }
}
