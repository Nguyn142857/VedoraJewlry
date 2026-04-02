package com.jewelry.jewelryshopbackend.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@ConditionalOnClass(Flyway.class)
@ConditionalOnProperty(prefix = "spring.flyway", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FlywayConfig {

    @Bean(name = "flyway", initMethod = "migrate")
    public Flyway flyway(DataSource dataSource, Environment environment) {
        FluentConfiguration configuration = Flyway.configure()
                .dataSource(dataSource)
                .baselineOnMigrate(true)
                .baselineVersion("0");
        String locations = environment.getProperty("spring.flyway.locations");
        if (locations != null && !locations.isBlank()) {
            configuration.locations(
                    Arrays.stream(locations.split(","))
                            .map(String::trim)
                            .filter(location -> !location.isBlank())
                            .toArray(String[]::new)
            );
        }
        return configuration.load();
    }

    @Bean
    public static BeanFactoryPostProcessor entityManagerFactoryDependsOnFlyway() {
        return new BeanFactoryPostProcessor() {
            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
                if (!beanFactory.containsBeanDefinition("entityManagerFactory")) {
                    return;
                }

                BeanDefinition beanDefinition = beanFactory.getBeanDefinition("entityManagerFactory");
                String[] currentDependsOn = beanDefinition.getDependsOn();
                if (currentDependsOn == null || currentDependsOn.length == 0) {
                    beanDefinition.setDependsOn("flyway");
                    return;
                }

                if (Arrays.stream(currentDependsOn).noneMatch("flyway"::equals)) {
                    String[] merged = Arrays.copyOf(currentDependsOn, currentDependsOn.length + 1);
                    merged[currentDependsOn.length] = "flyway";
                    beanDefinition.setDependsOn(merged);
                }
            }
        };
    }
}
