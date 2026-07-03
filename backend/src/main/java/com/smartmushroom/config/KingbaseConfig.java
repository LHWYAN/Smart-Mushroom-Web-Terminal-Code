package com.smartmushroom.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class KingbaseConfig {

    @Bean
    public DataSource kingbaseDataSource(AppProperties appProperties) {
        var k = appProperties.getKingbase();
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName("com.kingbase8.Driver");
        ds.setJdbcUrl("jdbc:kingbase8://" + k.getHost() + ":" + k.getPort() + "/" + k.getDatabase());
        ds.setUsername(k.getUsername());
        ds.setPassword(k.getPassword());
        ds.setMaximumPoolSize(5);
        ds.setMinimumIdle(1);
        ds.setConnectionTestQuery("SELECT 1");
        return ds;
    }

    @Bean
    public JdbcTemplate kingbaseJdbcTemplate(DataSource kingbaseDataSource) {
        return new JdbcTemplate(kingbaseDataSource);
    }
}
