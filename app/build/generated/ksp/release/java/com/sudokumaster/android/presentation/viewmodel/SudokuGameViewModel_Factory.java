package com.sudokumaster.android.presentation.viewmodel;

import com.sudokumaster.android.domain.repository.AuthRepository;
import com.sudokumaster.android.domain.repository.SudokuRepository;
import com.sudokumaster.android.utils.NetworkMonitor;
import com.sudokumaster.android.utils.PerformanceMonitor;
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
public final class SudokuGameViewModel_Factory implements Factory<SudokuGameViewModel> {
  private final Provider<SudokuRepository> sudokuRepositoryProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<NetworkMonitor> networkMonitorProvider;

  private final Provider<PerformanceMonitor> performanceMonitorProvider;

  public SudokuGameViewModel_Factory(Provider<SudokuRepository> sudokuRepositoryProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<NetworkMonitor> networkMonitorProvider,
      Provider<PerformanceMonitor> performanceMonitorProvider) {
    this.sudokuRepositoryProvider = sudokuRepositoryProvider;
    this.authRepositoryProvider = authRepositoryProvider;
    this.networkMonitorProvider = networkMonitorProvider;
    this.performanceMonitorProvider = performanceMonitorProvider;
  }

  @Override
  public SudokuGameViewModel get() {
    return newInstance(sudokuRepositoryProvider.get(), authRepositoryProvider.get(), networkMonitorProvider.get(), performanceMonitorProvider.get());
  }

  public static SudokuGameViewModel_Factory create(
      Provider<SudokuRepository> sudokuRepositoryProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<NetworkMonitor> networkMonitorProvider,
      Provider<PerformanceMonitor> performanceMonitorProvider) {
    return new SudokuGameViewModel_Factory(sudokuRepositoryProvider, authRepositoryProvider, networkMonitorProvider, performanceMonitorProvider);
  }

  public static SudokuGameViewModel newInstance(SudokuRepository sudokuRepository,
      AuthRepository authRepository, NetworkMonitor networkMonitor,
      PerformanceMonitor performanceMonitor) {
    return new SudokuGameViewModel(sudokuRepository, authRepository, networkMonitor, performanceMonitor);
  }
}
