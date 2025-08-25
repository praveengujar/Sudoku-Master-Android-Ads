package com.sudokumaster.android.di;

import android.content.Context;
import com.sudokumaster.android.data.local.SudokuDatabase;
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
public final class AppModule_ProvideSudokuDatabaseFactory implements Factory<SudokuDatabase> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvideSudokuDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SudokuDatabase get() {
    return provideSudokuDatabase(contextProvider.get());
  }

  public static AppModule_ProvideSudokuDatabaseFactory create(Provider<Context> contextProvider) {
    return new AppModule_ProvideSudokuDatabaseFactory(contextProvider);
  }

  public static SudokuDatabase provideSudokuDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideSudokuDatabase(context));
  }
}
