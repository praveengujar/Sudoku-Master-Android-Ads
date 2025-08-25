package com.sudokumaster.android.di;

import com.sudokumaster.android.data.local.AuthTokenStorage;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.Interceptor;

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
public final class AppModule_ProvideAuthInterceptorFactory implements Factory<Interceptor> {
  private final Provider<AuthTokenStorage> authTokenStorageProvider;

  public AppModule_ProvideAuthInterceptorFactory(
      Provider<AuthTokenStorage> authTokenStorageProvider) {
    this.authTokenStorageProvider = authTokenStorageProvider;
  }

  @Override
  public Interceptor get() {
    return provideAuthInterceptor(authTokenStorageProvider.get());
  }

  public static AppModule_ProvideAuthInterceptorFactory create(
      Provider<AuthTokenStorage> authTokenStorageProvider) {
    return new AppModule_ProvideAuthInterceptorFactory(authTokenStorageProvider);
  }

  public static Interceptor provideAuthInterceptor(AuthTokenStorage authTokenStorage) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAuthInterceptor(authTokenStorage));
  }
}
