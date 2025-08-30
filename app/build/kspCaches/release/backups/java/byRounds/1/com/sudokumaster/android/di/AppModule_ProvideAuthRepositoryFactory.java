package com.sudokumaster.android.di;

import com.sudokumaster.android.data.local.AuthTokenStorage;
import com.sudokumaster.android.data.remote.ApiService;
import com.sudokumaster.android.domain.repository.AuthRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideAuthRepositoryFactory implements Factory<AuthRepository> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<AuthTokenStorage> authTokenStorageProvider;

  public AppModule_ProvideAuthRepositoryFactory(Provider<ApiService> apiServiceProvider,
      Provider<AuthTokenStorage> authTokenStorageProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.authTokenStorageProvider = authTokenStorageProvider;
  }

  @Override
  public AuthRepository get() {
    return provideAuthRepository(apiServiceProvider.get(), authTokenStorageProvider.get());
  }

  public static AppModule_ProvideAuthRepositoryFactory create(
      Provider<ApiService> apiServiceProvider,
      Provider<AuthTokenStorage> authTokenStorageProvider) {
    return new AppModule_ProvideAuthRepositoryFactory(apiServiceProvider, authTokenStorageProvider);
  }

  public static AuthRepository provideAuthRepository(ApiService apiService,
      AuthTokenStorage authTokenStorage) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAuthRepository(apiService, authTokenStorage));
  }
}
