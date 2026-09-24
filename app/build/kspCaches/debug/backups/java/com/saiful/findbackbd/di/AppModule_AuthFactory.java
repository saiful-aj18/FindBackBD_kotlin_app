package com.saiful.findbackbd.di;

import com.google.firebase.auth.FirebaseAuth;
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
public final class AppModule_AuthFactory implements Factory<FirebaseAuth> {
  @Override
  public FirebaseAuth get() {
    return auth();
  }

  public static AppModule_AuthFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FirebaseAuth auth() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.auth());
  }

  private static final class InstanceHolder {
    static final AppModule_AuthFactory INSTANCE = new AppModule_AuthFactory();
  }
}
