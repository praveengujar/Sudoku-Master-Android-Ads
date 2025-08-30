package com.sudokumaster.android.data.repository;

import com.sudokumaster.android.data.local.SudokuDatabase;
import com.sudokumaster.android.data.remote.ApiService;
import com.sudokumaster.android.domain.repository.AuthRepository;
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
public final class SudokuRepositoryImpl_Factory implements Factory<SudokuRepositoryImpl> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<SudokuDatabase> databaseProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  public SudokuRepositoryImpl_Factory(Provider<ApiService> apiServiceProvider,
      Provider<SudokuDatabase> databaseProvider, Provider<AuthRepository> authRepositoryProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.databaseProvider = databaseProvider;
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public SudokuRepositoryImpl get() {
    return newInstance(apiServiceProvider.get(), databaseProvider.get(), authRepositoryProvider.get());
  }

  public static SudokuRepositoryImpl_Factory create(Provider<ApiService> apiServiceProvider,
      Provider<SudokuDatabase> databaseProvider, Provider<AuthRepository> authRepositoryProvider) {
    return new SudokuRepositoryImpl_Factory(apiServiceProvider, databaseProvider, authRepositoryProvider);
  }

  public static SudokuRepositoryImpl newInstance(ApiService apiService, SudokuDatabase database,
      AuthRepository authRepository) {
    return new SudokuRepositoryImpl(apiService, database, authRepository);
  }
}
