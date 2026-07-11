package com.example.bingwallpaper.db

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performBlocking
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
public class WallpaperDao_Impl(
  __db: RoomDatabase,
) : WallpaperDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfWallpaperEntity: EntityInsertAdapter<WallpaperEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfWallpaperEntity = object : EntityInsertAdapter<WallpaperEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `wallpapers` (`date`,`imageUrl`,`thumbnailUrl`,`copyright`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: WallpaperEntity) {
        statement.bindText(1, entity.date)
        statement.bindText(2, entity.imageUrl)
        statement.bindText(3, entity.thumbnailUrl)
        statement.bindText(4, entity.copyright)
      }
    }
  }

  public override fun insertAll(wallpapers: List<WallpaperEntity>): Unit = performBlocking(__db, false, true) { _connection ->
    __insertAdapterOfWallpaperEntity.insert(_connection, wallpapers)
  }

  public override fun getAll(): List<WallpaperEntity> {
    val _sql: String = "SELECT * FROM wallpapers ORDER BY date DESC"
    return performBlocking(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfThumbnailUrl: Int = getColumnIndexOrThrow(_stmt, "thumbnailUrl")
        val _columnIndexOfCopyright: Int = getColumnIndexOrThrow(_stmt, "copyright")
        val _result: MutableList<WallpaperEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: WallpaperEntity
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpImageUrl: String
          _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          val _tmpThumbnailUrl: String
          _tmpThumbnailUrl = _stmt.getText(_columnIndexOfThumbnailUrl)
          val _tmpCopyright: String
          _tmpCopyright = _stmt.getText(_columnIndexOfCopyright)
          _item = WallpaperEntity(_tmpDate,_tmpImageUrl,_tmpThumbnailUrl,_tmpCopyright)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun deleteAll() {
    val _sql: String = "DELETE FROM wallpapers"
    return performBlocking(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun deleteOldWallpapers() {
    val _sql: String = "DELETE FROM wallpapers WHERE date NOT IN (SELECT date FROM wallpapers ORDER BY date DESC LIMIT 8)"
    return performBlocking(__db, false, true) { _connection ->
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
