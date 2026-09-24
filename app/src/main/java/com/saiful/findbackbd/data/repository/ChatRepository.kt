package com.saiful.findbackbd.data.repository
import com.google.firebase.firestore.FirebaseFirestore
import com.saiful.findbackbd.data.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
class ChatRepository @Inject constructor(private val db:FirebaseFirestore){
 fun messages(chatId:String):Flow<List<Message>> = callbackFlow{val reg:ListenerRegistration=db.collection("chats").document(chatId).collection("messages").orderBy("timestamp").addSnapshotListener{snap,e->if(e!=null){close(e);return@addSnapshotListener};trySend(snap?.toObjects(Message::class.java).orEmpty())};awaitClose{reg.remove()}}
 suspend fun send(chatId:String,message:Message):Result<Unit>{return try{db.collection("chats").document(chatId).collection("messages").add(message).await();Result.success(Unit)}catch(e:Exception){Result.failure(e)}}
}
