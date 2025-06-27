package com.example.training.utils;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import com.google.cloud.spring.autoconfigure.datastore.GcpDatastoreAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Configuration
@AutoConfigureBefore(GcpDatastoreAutoConfiguration.class)
@ConditionalOnMissingBean(LocalDatastoreHelper.class)
@ConditionalOnProperty(value = "aw.datastore.emulator.enabled")
@SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
public class LocalDatastoreTestsAutoConfiguration {

    private LocalDatastoreHelper localDatastoreHelper;

    @Bean
    public LocalDatastoreHelper localDatastoreHelper() throws IOException, InterruptedException {
        localDatastoreHelper = LocalDatastoreHelper.newBuilder()
                .setConsistency(1.0)
                .setStoreOnDisk(false)
                .setFirestoreInDatastoreMode(true)
                .build();
        localDatastoreHelper.start();
        return localDatastoreHelper;
    }

    @Bean
    public Datastore testDatastore(LocalDatastoreHelper localDatastoreHelper) {
        return localDatastoreHelper.getOptions().getService();
    }

    @PreDestroy
    public void cleanup() throws IOException, InterruptedException, TimeoutException {
        if (localDatastoreHelper != null) {
            localDatastoreHelper.stop();
        }
    }
}
