package com.saiful.findbackbd.ui.screens.profile;

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
public final class ProfileViewModel_Factory implements Factory<ProfileViewModel> {
  private final Provider<AuthRepository> repoProvider;

  private ProfileViewModel_Factory(Provider<AuthRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public ProfileViewModel get() {
    return newInstance(repoProvider.get());
  }

  public static ProfileViewModel_Factory create(Provider<AuthRepository> repoProvider) {
    return new ProfileViewModel_Factory(repoProvider);
  }

  public static ProfileViewModel newInstance(AuthRepository repo) {
    return new ProfileViewModel(repo);
  }
}
