package com.saiful.findbackbd.`data`.local

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ItemDao_Impl(
  __db: RoomDatabase,
) : ItemDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfItemEntity: EntityInsertAdapter<ItemEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfItemEntity = object : EntityInsertAdapter<ItemEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `items` (`id`,`title`,`type`,`category`,`description`,`locationName`,`date`,`time`,`imageUrl`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ItemEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.type)
        statement.bindText(4, entity.category)
        statement.bindText(5, entity.description)
        statement.bindText(6, entity.locationName)
        statement.bindText(7, entity.date)
        statement.bindText(8, entity.time)
        statement.bindText(9, entity.imageUrl)
      }
    }
  }

  public override suspend fun insert(item: ItemEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfItemEntity.insert(_connection, item)
  }

  public override suspend fun getAll(): List<ItemEntity> {
    val _sql: String = "SELECT * FROM items"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfTime: Int = getColumnIndexOrThrow(_stmt, "time")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _result: MutableList<ItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ItemEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpLocationName: String
          _tmpLocationName = _stmt.getText(_columnIndexOfLocationName)
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpTime: String
          _tmpTime = _stmt.getText(_columnIndexOfTime)
          val _tmpImageUrl: String
          _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          _item = ItemEntity(_tmpId,_tmpTitle,_tmpType,_tmpCategory,_tmpDescription,_tmpLocationName,_tmpDate,_tmpTime,_tmpImageUrl)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clear() {
    val _sql: String = "DELETE FROM items"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
