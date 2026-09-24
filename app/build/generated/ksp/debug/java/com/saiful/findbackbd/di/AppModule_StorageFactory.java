package com.saiful.findbackbd.di;

import com.google.firebase.storage.FirebaseStorage;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class AppModule_StorageFactory implements Factory<FirebaseStorage> {
  @Override
  public FirebaseStorage get() {
    return storage();
  }

  public static AppModule_StorageFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FirebaseStorage storage() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.storage());
  }

  private static final class InstanceHolder {
    static final AppModule_StorageFactory INSTANCE = new AppModule_StorageFactory();
  }
}
