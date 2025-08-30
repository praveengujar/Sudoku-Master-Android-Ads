package com.sudokumaster.android.di;

import android.content.Context;
import com.sudokumaster.android.data.local.AuthTokenStorage;
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

  public AppModule_ProvideAuthTokenStorageFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AuthTokenStorage get() {
    return provideAuthTokenStorage(contextProvider.get());
  }

  public static AppModule_ProvideAuthTokenStorageFactory create(Provider<Context> contextProvider) {
    return new AppModule_ProvideAuthTokenStorageFactory(contextProvider);
  }

  public static AuthTokenStorage provideAuthTokenStorage(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAuthTokenStorage(context));
  }
}
