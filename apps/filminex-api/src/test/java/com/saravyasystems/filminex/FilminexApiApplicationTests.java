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
                        where version in ('1', '2', '3', '4', '5', '6', '7')
                          and success = true
                        """)
                .query(Long.class)
                .single();

        assertThat(appliedMigrations).isEqualTo(7L);
        assertThat(tableExists("workspace")).isTrue();
        assertThat(tableExists("project")).isTrue();
        assertThat(tableExists("event_outbox")).isTrue();
        assertThat(tableExists("workspace_membership")).isTrue();
        assertThat(tableExists("audit_event")).isTrue();
        assertThat(tableExists("workspace_entitlement")).isTrue();
        assertThat(tableExists("local_rights_grant")).isTrue();
        assertThat(tableExists("localization_track")).isTrue();
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
