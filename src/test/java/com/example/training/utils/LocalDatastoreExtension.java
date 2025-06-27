package com.example.training.utils;

import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class LocalDatastoreExtension implements BeforeAllCallback, BeforeEachCallback {

    @Override
    public void beforeAll(final ExtensionContext context) throws Exception {
        LocalDatastoreHelper helper = getHelper(context);
        helper.start();
    }

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        final LocalDatastoreHelper helper = getHelper(context);
        helper.reset();
    }

    public static LocalDatastoreHelper getHelper(final ExtensionContext context) {
        return SpringExtension.getApplicationContext(context).getBean(LocalDatastoreHelper.class);
    }
}
