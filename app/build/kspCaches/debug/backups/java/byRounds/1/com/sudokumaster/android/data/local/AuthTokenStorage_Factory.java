package com.sudokumaster.android.data.local;

import android.content.Context;
import com.sudokumaster.android.utils.BiometricAuthManager;
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
public final class AuthTokenStorage_Factory implements Factory<AuthTokenStorage> {
  private final Provider<Context> contextProvider;

  private final Provider<BiometricAuthManager> biometricAuthManagerProvider;

  public AuthTokenStorage_Factory(Provider<Context> contextProvider,
      Provider<BiometricAuthManager> biometricAuthManagerProvider) {
    this.contextProvider = contextProvider;
    this.biometricAuthManagerProvider = biometricAuthManagerProvider;
  }

  @Override
  public AuthTokenStorage get() {
    return newInstance(contextProvider.get(), biometricAuthManagerProvider.get());
  }

  public static AuthTokenStorage_Factory create(Provider<Context> contextProvider,
      Provider<BiometricAuthManager> biometricAuthManagerProvider) {
    return new AuthTokenStorage_Factory(contextProvider, biometricAuthManagerProvider);
  }

  public static AuthTokenStorage newInstance(Context context,
      BiometricAuthManager biometricAuthManager) {
    return new AuthTokenStorage(context, biometricAuthManager);
  }
}
