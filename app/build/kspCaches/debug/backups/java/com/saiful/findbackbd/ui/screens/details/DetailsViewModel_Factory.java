package com.saiful.findbackbd.ui.screens.details;

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
public final class DetailsViewModel_Factory implements Factory<DetailsViewModel> {
  private final Provider<ItemRepository> repoProvider;

  private DetailsViewModel_Factory(Provider<ItemRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public DetailsViewModel get() {
    return newInstance(repoProvider.get());
  }

  public static DetailsViewModel_Factory create(Provider<ItemRepository> repoProvider) {
    return new DetailsViewModel_Factory(repoProvider);
  }

  public static DetailsViewModel newInstance(ItemRepository repo) {
    return new DetailsViewModel(repo);
  }
}
