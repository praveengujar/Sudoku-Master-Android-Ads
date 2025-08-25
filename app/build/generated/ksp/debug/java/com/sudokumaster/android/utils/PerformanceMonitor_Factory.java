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
public final class PerformanceMonitor_Factory implements Factory<PerformanceMonitor> {
  private final Provider<Context> contextProvider;

  public PerformanceMonitor_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public PerformanceMonitor get() {
    return newInstance(contextProvider.get());
  }

  public static PerformanceMonitor_Factory create(Provider<Context> contextProvider) {
    return new PerformanceMonitor_Factory(contextProvider);
  }

  public static PerformanceMonitor newInstance(Context context) {
    return new PerformanceMonitor(context);
  }
}
