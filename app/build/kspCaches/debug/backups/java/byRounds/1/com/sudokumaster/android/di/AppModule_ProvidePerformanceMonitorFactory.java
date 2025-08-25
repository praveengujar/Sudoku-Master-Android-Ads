package com.sudokumaster.android.di;

import android.content.Context;
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
public final class AppModule_ProvidePerformanceMonitorFactory implements Factory<PerformanceMonitor> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvidePerformanceMonitorFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public PerformanceMonitor get() {
    return providePerformanceMonitor(contextProvider.get());
  }

  public static AppModule_ProvidePerformanceMonitorFactory create(
      Provider<Context> contextProvider) {
    return new AppModule_ProvidePerformanceMonitorFactory(contextProvider);
  }

  public static PerformanceMonitor providePerformanceMonitor(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.providePerformanceMonitor(context));
  }
}
