package com.sudokumaster.android.presentation.viewmodel;

import com.sudokumaster.android.domain.repository.AuthRepository;
import com.sudokumaster.android.utils.NetworkMonitor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<NetworkMonitor> networkMonitorProvider;

  public AuthViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<NetworkMonitor> networkMonitorProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.networkMonitorProvider = networkMonitorProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(authRepositoryProvider.get(), networkMonitorProvider.get());
  }

  public static AuthViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<NetworkMonitor> networkMonitorProvider) {
    return new AuthViewModel_Factory(authRepositoryProvider, networkMonitorProvider);
  }

  public static AuthViewModel newInstance(AuthRepository authRepository,
      NetworkMonitor networkMonitor) {
    return new AuthViewModel(authRepository, networkMonitor);
  }
}
