package com.sudokumaster.android.di;

import android.content.Context;
import com.sudokumaster.android.data.local.AuthTokenStorage;
import com.sudokumaster.android.utils.BiometricAuthManager;
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
public final class AppModule_ProvideAuthTokenStorageFactory implements Factory<AuthTokenStorage> {
  private final Provider<Context> contextProvider;

  private final Provider<BiometricAuthManager> biometricAuthManagerProvider;

  public AppModule_ProvideAuthTokenStorageFactory(Provider<Context> contextProvider,
      Provider<BiometricAuthManager> biometricAuthManagerProvider) {
    this.contextProvider = contextProvider;
    this.biometricAuthManagerProvider = biometricAuthManagerProvider;
  }

  @Override
  public AuthTokenStorage get() {
    return provideAuthTokenStorage(contextProvider.get(), biometricAuthManagerProvider.get());
  }

  public static AppModule_ProvideAuthTokenStorageFactory create(Provider<Context> contextProvider,
      Provider<BiometricAuthManager> biometricAuthManagerProvider) {
    return new AppModule_ProvideAuthTokenStorageFactory(contextProvider, biometricAuthManagerProvider);
  }

  public static AuthTokenStorage provideAuthTokenStorage(Context context,
      BiometricAuthManager biometricAuthManager) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAuthTokenStorage(context, biometricAuthManager));
  }
}
