package com.sudokumaster.android.utils;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
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
public final class AdManagerStub_Factory implements Factory<AdManagerStub> {
  private final Provider<Context> contextProvider;

  private final Provider<PerformanceMonitor> performanceMonitorProvider;

  public AdManagerStub_Factory(Provider<Context> contextProvider,
      Provider<PerformanceMonitor> performanceMonitorProvider) {
    this.contextProvider = contextProvider;
    this.performanceMonitorProvider = performanceMonitorProvider;
  }

  @Override
  public AdManagerStub get() {
    return newInstance(contextProvider.get(), performanceMonitorProvider.get());
  }

  public static AdManagerStub_Factory create(Provider<Context> contextProvider,
      Provider<PerformanceMonitor> performanceMonitorProvider) {
    return new AdManagerStub_Factory(contextProvider, performanceMonitorProvider);
  }

  public static AdManagerStub newInstance(Context context, PerformanceMonitor performanceMonitor) {
    return new AdManagerStub(context, performanceMonitor);
  }
}
