package com.example.bingwallpaper.db

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _wallpaperDao: Lazy<WallpaperDao> = lazy {
    WallpaperDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2, "6ad4e53b7d6e68db205cf4c9326d5b21", "277d67102b6fa5042162c8fbb450fb98") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `wallpapers` (`date` TEXT NOT NULL, `imageUrl` TEXT NOT NULL, `thumbnailUrl` TEXT NOT NULL, `copyright` TEXT NOT NULL, PRIMARY KEY(`date`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '6ad4e53b7d6e68db205cf4c9326d5b21')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `wallpapers`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsWallpapers: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsWallpapers.put("date", TableInfo.Column("date", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWallpapers.put("imageUrl", TableInfo.Column("imageUrl", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWallpapers.put("thumbnailUrl", TableInfo.Column("thumbnailUrl", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWallpapers.put("copyright", TableInfo.Column("copyright", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysWallpapers: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesWallpapers: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoWallpapers: TableInfo = TableInfo("wallpapers", _columnsWallpapers, _foreignKeysWallpapers, _indicesWallpapers)
        val _existingWallpapers: TableInfo = read(connection, "wallpapers")
        if (!_infoWallpapers.equals(_existingWallpapers)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |wallpapers(com.example.bingwallpaper.db.WallpaperEntity).
              | Expected:
              |""".trimMargin() + _infoWallpapers + """
              |
              | Found:
              |""".trimMargin() + _existingWallpapers)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "wallpapers")
  }

  public override fun clearAllTables() {
    super.performClear(false, "wallpapers")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(WallpaperDao::class, WallpaperDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun wallpaperDao(): WallpaperDao = _wallpaperDao.value
}
