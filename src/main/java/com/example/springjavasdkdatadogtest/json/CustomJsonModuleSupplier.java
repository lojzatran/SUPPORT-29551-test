package com.example.springjavasdkdatadogtest.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.vrap.rmf.base.client.utils.json.ModuleSupplier;
import io.vrap.rmf.base.client.utils.json.modules.ModuleOptions;

public class CustomJsonModuleSupplier implements ModuleSupplier {

    @Override
    public SimpleModule getModule(final ModuleOptions moduleOptions) {
        return new CustomJsonModule(moduleOptions);
    }
}
