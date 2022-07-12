package org.partymaker.matchmaker.configuration.datasource

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import javax.sql.DataSource

const val MAIN_TM = "mainTransactionManager"

@Configuration
@Profile("common")
@EnableJpaRepositories(
    basePackages = ["org.partymaker.matchmaker.entity"],
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = MAIN_TM
)
class MainDataSourceConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.main")
    fun mainDataSourceProperties() = DataSourceProperties()

    @Bean
    @Primary
    fun mainDataSource(): DataSource =
        mainDataSourceProperties()
            .initializeDataSourceBuilder()
            .build()

    @Bean
    @ConfigurationProperties(prefix = "spring.jpa.main")
    fun mainJpaProperties() = JpaProperties()

    @Bean
    @Primary
    fun entityManagerFactory(): LocalContainerEntityManagerFactoryBean =
        hibernateBuilder(mainJpaProperties())
            .dataSource(mainDataSource())
            .packages("org.partymaker.matchmaker.entity")
            .persistenceUnit("main")
            .build()

    @Bean
    @Primary
    fun mainTransactionManager() = JpaTransactionManager(entityManagerFactory().`object`!!)

    fun hibernateBuilder(jpaProperties: JpaProperties, persistenceUnitManager: PersistenceUnitManager? = null) =
        EntityManagerFactoryBuilder(
            hibernateJpaVendorAdapter(jpaProperties),
            jpaProperties.properties,
            persistenceUnitManager
        )

    private fun hibernateJpaVendorAdapter(jpaProperties: JpaProperties) =
        HibernateJpaVendorAdapter().apply {
            setShowSql(jpaProperties.isShowSql)
        }
}
