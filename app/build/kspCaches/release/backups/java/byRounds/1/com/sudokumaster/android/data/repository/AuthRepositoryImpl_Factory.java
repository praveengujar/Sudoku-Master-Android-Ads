package com.sudokumaster.android.data.repository;

import com.sudokumaster.android.data.local.AuthTokenStorage;
import com.sudokumaster.android.data.remote.ApiService;
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
public final class AuthRepositoryImpl_Factory implements Factory<AuthRepositoryImpl> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<AuthTokenStorage> authTokenStorageProvider;

  public AuthRepositoryImpl_Factory(Provider<ApiService> apiServiceProvider,
      Provider<AuthTokenStorage> authTokenStorageProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.authTokenStorageProvider = authTokenStorageProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(apiServiceProvider.get(), authTokenStorageProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(Provider<ApiService> apiServiceProvider,
      Provider<AuthTokenStorage> authTokenStorageProvider) {
    return new AuthRepositoryImpl_Factory(apiServiceProvider, authTokenStorageProvider);
  }

  public static AuthRepositoryImpl newInstance(ApiService apiService,
      AuthTokenStorage authTokenStorage) {
    return new AuthRepositoryImpl(apiService, authTokenStorage);
  }
}
