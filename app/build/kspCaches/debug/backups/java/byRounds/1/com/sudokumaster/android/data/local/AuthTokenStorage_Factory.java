package com.sudokumaster.android.data.local;

import android.content.Context;
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

  public AuthTokenStorage_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AuthTokenStorage get() {
    return newInstance(contextProvider.get());
  }

  public static AuthTokenStorage_Factory create(Provider<Context> contextProvider) {
    return new AuthTokenStorage_Factory(contextProvider);
  }

  public static AuthTokenStorage newInstance(Context context) {
    return new AuthTokenStorage(context);
  }
}
