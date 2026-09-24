package com.saiful.findbackbd.data.local
import androidx.room.*
@Entity(tableName="items") data class ItemEntity(@PrimaryKey val id:String,val title:String,val type:String,val category:String,val description:String,val locationName:String,val date:String,val time:String,val imageUrl:String="")
@Dao interface ItemDao{@Query("SELECT * FROM items") suspend fun getAll():List<ItemEntity>;@Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun insert(item:ItemEntity);@Query("DELETE FROM items") suspend fun clear()}
@Database(entities=[ItemEntity::class],version=1,exportSchema=false) abstract class AppDatabase:RoomDatabase(){abstract fun itemDao():ItemDao}
