package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class databaseconfig {

    @Bean
    public DatabaseServiceCredentials databaseServiceCredentials (@Value("${vcap.services}") String vcap ){

        return new DatabaseServiceCredentials(vcap);
    }

    @Bean
    public DataSource movieDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql"));
        HikariConfig config = new HikariConfig();
        config.setDataSource(dataSource);
        return new HikariDataSource(config);
    }


    @Bean//(value="albumsDataSource")
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql"));
        HikariConfig config = new HikariConfig();
        config.setDataSource(dataSource);
        return new HikariDataSource(config);
    }


    @Bean
    HibernateJpaVendorAdapter hibernatejpavendor ()
    {
        HibernateJpaVendorAdapter hjpa= new HibernateJpaVendorAdapter();

        hjpa.setDatabase(Database.MYSQL);
        hjpa.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        hjpa.setGenerateDdl(true);

        return  hjpa;
    }

    @Bean
    //@Qualifier("albumsDataSource")
    LocalContainerEntityManagerFactoryBean albumentitymanagerbean(DataSource albumsDataSource , HibernateJpaVendorAdapter jpavendoradapter){

        LocalContainerEntityManagerFactoryBean a = new LocalContainerEntityManagerFactoryBean();
        a.setDataSource(albumsDataSource);
        a.setJpaVendorAdapter(jpavendoradapter);
        a.setPackagesToScan("org.superbiz.moviefun.albums");
        a.setPersistenceUnitName("albums-unit");

        return a;
    }

    @Bean
    //@Qualifier("movieDataSource")
    LocalContainerEntityManagerFactoryBean movieentitymanagerbean(DataSource movieDataSource , HibernateJpaVendorAdapter jpavendoradapter){

        LocalContainerEntityManagerFactoryBean a = new LocalContainerEntityManagerFactoryBean();
        a.setDataSource(movieDataSource);
        a.setJpaVendorAdapter(jpavendoradapter);
        a.setPackagesToScan("org.superbiz.moviefun.movies");
        a.setPersistenceUnitName("movies-unit");

        return a;
    }

@Bean
PlatformTransactionManager albumsPlattranactionmanager(EntityManagerFactory albumentitymanagerbean){


    JpaTransactionManager jtm = new JpaTransactionManager();
    jtm.setEntityManagerFactory(albumentitymanagerbean);


        return jtm;
}

    @Bean
    PlatformTransactionManager moviesPlattranactionmanager(EntityManagerFactory movieentitymanagerbean){


        JpaTransactionManager jtm = new JpaTransactionManager();
        jtm.setEntityManagerFactory(movieentitymanagerbean);


        return jtm;
    }

}


