package com.pivot13;

import com.google.inject.Module;
import roboguice.application.RoboInjectableApplication;
import roboguice.config.AbstractAndroidModule;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class C2DeMoApplication extends RoboInjectableApplication {
    public static final String C2DM_SENDER_KEY = "c2dm@pivot13.com";
    private Module module = new ApplicationModule();

    @Override
    protected void addApplicationModules(List<Module> modules) {
        modules.add(module);
    }

    /* just for test injection */
    public void setModule(Module module) {
        this.module = module;
    }

    public static class ApplicationModule extends AbstractAndroidModule {
        @Override
        protected void configure() {
            final ExecutorService executorService = Executors.newFixedThreadPool(5);
            bind(Executor.class).toInstance(executorService);
            bind(ExecutorService.class).toInstance(executorService);

            /*Samples of injection binding*/
            //        bind(FooBar.class).in(Scopes.SINGLETON);
            //        bind(Date.class).toProvider(FakeDateProvider.class);
            //        bind(Ln.BaseConfig.class).toInstance(new SampleLoggerConfig());
        }
    }
}
