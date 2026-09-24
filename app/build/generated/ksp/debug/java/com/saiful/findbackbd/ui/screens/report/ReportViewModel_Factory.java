package com.saiful.findbackbd.ui.screens.report;

import com.saiful.findbackbd.data.repository.AuthRepository;
import com.saiful.findbackbd.data.repository.ItemRepository;
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
public final class ReportViewModel_Factory implements Factory<ReportViewModel> {
  private final Provider<ItemRepository> repoProvider;

  private final Provider<AuthRepository> authProvider;

  private ReportViewModel_Factory(Provider<ItemRepository> repoProvider,
      Provider<AuthRepository> authProvider) {
    this.repoProvider = repoProvider;
    this.authProvider = authProvider;
  }

  @Override
  public ReportViewModel get() {
    return newInstance(repoProvider.get(), authProvider.get());
  }

  public static ReportViewModel_Factory create(Provider<ItemRepository> repoProvider,
      Provider<AuthRepository> authProvider) {
    return new ReportViewModel_Factory(repoProvider, authProvider);
  }

  public static ReportViewModel newInstance(ItemRepository repo, AuthRepository auth) {
    return new ReportViewModel(repo, auth);
  }
}
