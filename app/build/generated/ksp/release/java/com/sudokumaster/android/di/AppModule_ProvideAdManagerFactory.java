package com.sudokumaster.android.di;

import android.content.Context;
import com.sudokumaster.android.utils.AdManagerStub;
import com.sudokumaster.android.utils.PerformanceMonitor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class AppModule_ProvideAdManagerFactory implements Factory<AdManagerStub> {
  private final Provider<Context> contextProvider;

  private final Provider<PerformanceMonitor> performanceMonitorProvider;

  public AppModule_ProvideAdManagerFactory(Provider<Context> contextProvider,
      Provider<PerformanceMonitor> performanceMonitorProvider) {
    this.contextProvider = contextProvider;
    this.performanceMonitorProvider = performanceMonitorProvider;
  }

  @Override
  public AdManagerStub get() {
    return provideAdManager(contextProvider.get(), performanceMonitorProvider.get());
  }

  public static AppModule_ProvideAdManagerFactory create(Provider<Context> contextProvider,
      Provider<PerformanceMonitor> performanceMonitorProvider) {
    return new AppModule_ProvideAdManagerFactory(contextProvider, performanceMonitorProvider);
  }

  public static AdManagerStub provideAdManager(Context context,
      PerformanceMonitor performanceMonitor) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAdManager(context, performanceMonitor));
  }
}
