package com.sudokumaster.android.presentation.viewmodel;

import android.content.Context;
import com.sudokumaster.android.domain.repository.AuthRepository;
import com.sudokumaster.android.utils.BiometricAuthManager;
import com.sudokumaster.android.utils.NetworkMonitor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<NetworkMonitor> networkMonitorProvider;

  private final Provider<BiometricAuthManager> biometricAuthManagerProvider;

  public AuthViewModel_Factory(Provider<Context> contextProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<NetworkMonitor> networkMonitorProvider,
      Provider<BiometricAuthManager> biometricAuthManagerProvider) {
    this.contextProvider = contextProvider;
    this.authRepositoryProvider = authRepositoryProvider;
    this.networkMonitorProvider = networkMonitorProvider;
    this.biometricAuthManagerProvider = biometricAuthManagerProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(contextProvider.get(), authRepositoryProvider.get(), networkMonitorProvider.get(), biometricAuthManagerProvider.get());
  }

  public static AuthViewModel_Factory create(Provider<Context> contextProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<NetworkMonitor> networkMonitorProvider,
      Provider<BiometricAuthManager> biometricAuthManagerProvider) {
    return new AuthViewModel_Factory(contextProvider, authRepositoryProvider, networkMonitorProvider, biometricAuthManagerProvider);
  }

  public static AuthViewModel newInstance(Context context, AuthRepository authRepository,
      NetworkMonitor networkMonitor, BiometricAuthManager biometricAuthManager) {
    return new AuthViewModel(context, authRepository, networkMonitor, biometricAuthManager);
  }
}
