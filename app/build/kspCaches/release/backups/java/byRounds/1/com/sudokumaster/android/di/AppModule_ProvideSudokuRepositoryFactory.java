package com.sudokumaster.android.di;

import com.sudokumaster.android.data.local.SudokuDatabase;
import com.sudokumaster.android.data.remote.ApiService;
import com.sudokumaster.android.domain.repository.AuthRepository;
import com.sudokumaster.android.domain.repository.SudokuRepository;
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
public final class AppModule_ProvideSudokuRepositoryFactory implements Factory<SudokuRepository> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<SudokuDatabase> databaseProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  public AppModule_ProvideSudokuRepositoryFactory(Provider<ApiService> apiServiceProvider,
      Provider<SudokuDatabase> databaseProvider, Provider<AuthRepository> authRepositoryProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.databaseProvider = databaseProvider;
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public SudokuRepository get() {
    return provideSudokuRepository(apiServiceProvider.get(), databaseProvider.get(), authRepositoryProvider.get());
  }

  public static AppModule_ProvideSudokuRepositoryFactory create(
      Provider<ApiService> apiServiceProvider, Provider<SudokuDatabase> databaseProvider,
      Provider<AuthRepository> authRepositoryProvider) {
    return new AppModule_ProvideSudokuRepositoryFactory(apiServiceProvider, databaseProvider, authRepositoryProvider);
  }

  public static SudokuRepository provideSudokuRepository(ApiService apiService,
      SudokuDatabase database, AuthRepository authRepository) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideSudokuRepository(apiService, database, authRepository));
  }
}
