package com.sudokumaster.android.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SudokuDatabase_Impl extends SudokuDatabase {
  private volatile OfflinePuzzleDao _offlinePuzzleDao;

  private volatile GameProgressDao _gameProgressDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `offline_puzzles` (`id` INTEGER NOT NULL, `grid` TEXT NOT NULL, `solution` TEXT NOT NULL, `difficulty` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `game_progress` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `puzzleId` INTEGER NOT NULL, `userId` INTEGER, `currentGrid` TEXT NOT NULL, `originalGrid` TEXT NOT NULL, `difficulty` TEXT NOT NULL, `isCompleted` INTEGER NOT NULL, `timeSpentSeconds` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '62f947a57c3e9a678e6f5d4c44114729')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `offline_puzzles`");
        db.execSQL("DROP TABLE IF EXISTS `game_progress`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsOfflinePuzzles = new HashMap<String, TableInfo.Column>(5);
        _columnsOfflinePuzzles.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflinePuzzles.put("grid", new TableInfo.Column("grid", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflinePuzzles.put("solution", new TableInfo.Column("solution", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflinePuzzles.put("difficulty", new TableInfo.Column("difficulty", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflinePuzzles.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysOfflinePuzzles = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesOfflinePuzzles = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoOfflinePuzzles = new TableInfo("offline_puzzles", _columnsOfflinePuzzles, _foreignKeysOfflinePuzzles, _indicesOfflinePuzzles);
        final TableInfo _existingOfflinePuzzles = TableInfo.read(db, "offline_puzzles");
        if (!_infoOfflinePuzzles.equals(_existingOfflinePuzzles)) {
          return new RoomOpenHelper.ValidationResult(false, "offline_puzzles(com.sudokumaster.android.data.local.OfflinePuzzleEntity).\n"
                  + " Expected:\n" + _infoOfflinePuzzles + "\n"
                  + " Found:\n" + _existingOfflinePuzzles);
        }
        final HashMap<String, TableInfo.Column> _columnsGameProgress = new HashMap<String, TableInfo.Column>(9);
        _columnsGameProgress.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGameProgress.put("puzzleId", new TableInfo.Column("puzzleId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGameProgress.put("userId", new TableInfo.Column("userId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGameProgress.put("currentGrid", new TableInfo.Column("currentGrid", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGameProgress.put("originalGrid", new TableInfo.Column("originalGrid", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGameProgress.put("difficulty", new TableInfo.Column("difficulty", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGameProgress.put("isCompleted", new TableInfo.Column("isCompleted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGameProgress.put("timeSpentSeconds", new TableInfo.Column("timeSpentSeconds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGameProgress.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysGameProgress = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesGameProgress = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoGameProgress = new TableInfo("game_progress", _columnsGameProgress, _foreignKeysGameProgress, _indicesGameProgress);
        final TableInfo _existingGameProgress = TableInfo.read(db, "game_progress");
        if (!_infoGameProgress.equals(_existingGameProgress)) {
          return new RoomOpenHelper.ValidationResult(false, "game_progress(com.sudokumaster.android.data.local.GameProgressEntity).\n"
                  + " Expected:\n" + _infoGameProgress + "\n"
                  + " Found:\n" + _existingGameProgress);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "62f947a57c3e9a678e6f5d4c44114729", "0310ee2d701d24e2e332f4f9e3f76317");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "offline_puzzles","game_progress");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `offline_puzzles`");
      _db.execSQL("DELETE FROM `game_progress`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(OfflinePuzzleDao.class, OfflinePuzzleDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(GameProgressDao.class, GameProgressDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public OfflinePuzzleDao offlinePuzzleDao() {
    if (_offlinePuzzleDao != null) {
      return _offlinePuzzleDao;
    } else {
      synchronized(this) {
        if(_offlinePuzzleDao == null) {
          _offlinePuzzleDao = new OfflinePuzzleDao_Impl(this);
        }
        return _offlinePuzzleDao;
      }
    }
  }

  @Override
  public GameProgressDao gameProgressDao() {
    if (_gameProgressDao != null) {
      return _gameProgressDao;
    } else {
      synchronized(this) {
        if(_gameProgressDao == null) {
          _gameProgressDao = new GameProgressDao_Impl(this);
        }
        return _gameProgressDao;
      }
    }
  }
}
