package com.saiful.findbackbd.ui.screens.chat;

import com.saiful.findbackbd.data.repository.AuthRepository;
import com.saiful.findbackbd.data.repository.ChatRepository;
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
public final class ChatViewModel_Factory implements Factory<ChatViewModel> {
  private final Provider<ChatRepository> repoProvider;

  private final Provider<AuthRepository> authProvider;

  private ChatViewModel_Factory(Provider<ChatRepository> repoProvider,
      Provider<AuthRepository> authProvider) {
    this.repoProvider = repoProvider;
    this.authProvider = authProvider;
  }

  @Override
  public ChatViewModel get() {
    return newInstance(repoProvider.get(), authProvider.get());
  }

  public static ChatViewModel_Factory create(Provider<ChatRepository> repoProvider,
      Provider<AuthRepository> authProvider) {
    return new ChatViewModel_Factory(repoProvider, authProvider);
  }

  public static ChatViewModel newInstance(ChatRepository repo, AuthRepository auth) {
    return new ChatViewModel(repo, auth);
  }
}
