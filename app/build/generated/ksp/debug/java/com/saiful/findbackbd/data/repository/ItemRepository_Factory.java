package com.saiful.findbackbd.data.repository;

import com.google.firebase.firestore.FirebaseFirestore;
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
public final class ItemRepository_Factory implements Factory<ItemRepository> {
  private final Provider<FirebaseFirestore> dbProvider;

  private ItemRepository_Factory(Provider<FirebaseFirestore> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ItemRepository get() {
    return newInstance(dbProvider.get());
  }

  public static ItemRepository_Factory create(Provider<FirebaseFirestore> dbProvider) {
    return new ItemRepository_Factory(dbProvider);
  }

  public static ItemRepository newInstance(FirebaseFirestore db) {
    return new ItemRepository(db);
  }
}
