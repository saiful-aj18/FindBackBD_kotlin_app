package com.saiful.findbackbd.ui.screens.auth;

import com.saiful.findbackbd.data.repository.AuthRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<AuthRepository> repoProvider;

  private AuthViewModel_Factory(Provider<AuthRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(repoProvider.get());
  }

  public static AuthViewModel_Factory create(Provider<AuthRepository> repoProvider) {
    return new AuthViewModel_Factory(repoProvider);
  }

  public static AuthViewModel newInstance(AuthRepository repo) {
    return new AuthViewModel(repo);
  }
}
