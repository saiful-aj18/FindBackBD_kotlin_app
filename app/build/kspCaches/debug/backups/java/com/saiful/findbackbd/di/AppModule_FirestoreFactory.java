package com.saiful.findbackbd.di;

import com.google.firebase.firestore.FirebaseFirestore;
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
public final class AppModule_FirestoreFactory implements Factory<FirebaseFirestore> {
  @Override
  public FirebaseFirestore get() {
    return firestore();
  }

  public static AppModule_FirestoreFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FirebaseFirestore firestore() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.firestore());
  }

  private static final class InstanceHolder {
    static final AppModule_FirestoreFactory INSTANCE = new AppModule_FirestoreFactory();
  }
}
