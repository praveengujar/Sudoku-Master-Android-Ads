package com.sudokumaster.android.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.sudokumaster.android.domain.model.SudokuDifficulty;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class GameProgressDao_Impl implements GameProgressDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<GameProgressEntity> __insertionAdapterOfGameProgressEntity;

  private final Converters __converters = new Converters();

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldProgress;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllProgress;

  public GameProgressDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfGameProgressEntity = new EntityInsertionAdapter<GameProgressEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `game_progress` (`id`,`puzzleId`,`userId`,`currentGrid`,`originalGrid`,`difficulty`,`isCompleted`,`timeSpentSeconds`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GameProgressEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getPuzzleId());
        if (entity.getUserId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getUserId());
        }
        final String _tmp = __converters.fromSudokuGrid(entity.getCurrentGrid());
        statement.bindString(4, _tmp);
        final String _tmp_1 = __converters.fromSudokuGrid(entity.getOriginalGrid());
        statement.bindString(5, _tmp_1);
        final String _tmp_2 = __converters.fromDifficulty(entity.getDifficulty());
        statement.bindString(6, _tmp_2);
        final int _tmp_3 = entity.isCompleted() ? 1 : 0;
        statement.bindLong(7, _tmp_3);
        statement.bindLong(8, entity.getTimeSpentSeconds());
        final Long _tmp_4 = __converters.fromDate(entity.getTimestamp());
        if (_tmp_4 == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, _tmp_4);
        }
      }
    };
    this.__preparedStmtOfDeleteOldProgress = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM game_progress WHERE timestamp < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllProgress = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM game_progress";
        return _query;
      }
    };
  }

  @Override
  public Object insertProgress(final GameProgressEntity progress,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfGameProgressEntity.insert(progress);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldProgress(final Date expireTime,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldProgress.acquire();
        int _argIndex = 1;
        final Long _tmp = __converters.fromDate(expireTime);
        if (_tmp == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, _tmp);
        }
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteOldProgress.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllProgress(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllProgress.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAllProgress.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllProgress(final Continuation<? super List<GameProgressEntity>> $completion) {
    final String _sql = "SELECT * FROM game_progress ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<GameProgressEntity>>() {
      @Override
      @NonNull
      public List<GameProgressEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPuzzleId = CursorUtil.getColumnIndexOrThrow(_cursor, "puzzleId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfCurrentGrid = CursorUtil.getColumnIndexOrThrow(_cursor, "currentGrid");
          final int _cursorIndexOfOriginalGrid = CursorUtil.getColumnIndexOrThrow(_cursor, "originalGrid");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfTimeSpentSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "timeSpentSeconds");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<GameProgressEntity> _result = new ArrayList<GameProgressEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GameProgressEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpPuzzleId;
            _tmpPuzzleId = _cursor.getInt(_cursorIndexOfPuzzleId);
            final Integer _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            }
            final Integer[][] _tmpCurrentGrid;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfCurrentGrid);
            _tmpCurrentGrid = __converters.toSudokuGrid(_tmp);
            final Integer[][] _tmpOriginalGrid;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfOriginalGrid);
            _tmpOriginalGrid = __converters.toSudokuGrid(_tmp_1);
            final SudokuDifficulty _tmpDifficulty;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfDifficulty);
            _tmpDifficulty = __converters.toDifficulty(_tmp_2);
            final boolean _tmpIsCompleted;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_3 != 0;
            final int _tmpTimeSpentSeconds;
            _tmpTimeSpentSeconds = _cursor.getInt(_cursorIndexOfTimeSpentSeconds);
            final Date _tmpTimestamp;
            final Long _tmp_4;
            if (_cursor.isNull(_cursorIndexOfTimestamp)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfTimestamp);
            }
            final Date _tmp_5 = __converters.toDate(_tmp_4);
            if (_tmp_5 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.Date', but it was NULL.");
            } else {
              _tmpTimestamp = _tmp_5;
            }
            _item = new GameProgressEntity(_tmpId,_tmpPuzzleId,_tmpUserId,_tmpCurrentGrid,_tmpOriginalGrid,_tmpDifficulty,_tmpIsCompleted,_tmpTimeSpentSeconds,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getProgressByUser(final int userId,
      final Continuation<? super List<GameProgressEntity>> $completion) {
    final String _sql = "SELECT * FROM game_progress WHERE userId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<GameProgressEntity>>() {
      @Override
      @NonNull
      public List<GameProgressEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPuzzleId = CursorUtil.getColumnIndexOrThrow(_cursor, "puzzleId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfCurrentGrid = CursorUtil.getColumnIndexOrThrow(_cursor, "currentGrid");
          final int _cursorIndexOfOriginalGrid = CursorUtil.getColumnIndexOrThrow(_cursor, "originalGrid");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfTimeSpentSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "timeSpentSeconds");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<GameProgressEntity> _result = new ArrayList<GameProgressEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GameProgressEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpPuzzleId;
            _tmpPuzzleId = _cursor.getInt(_cursorIndexOfPuzzleId);
            final Integer _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            }
            final Integer[][] _tmpCurrentGrid;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfCurrentGrid);
            _tmpCurrentGrid = __converters.toSudokuGrid(_tmp);
            final Integer[][] _tmpOriginalGrid;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfOriginalGrid);
            _tmpOriginalGrid = __converters.toSudokuGrid(_tmp_1);
            final SudokuDifficulty _tmpDifficulty;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfDifficulty);
            _tmpDifficulty = __converters.toDifficulty(_tmp_2);
            final boolean _tmpIsCompleted;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_3 != 0;
            final int _tmpTimeSpentSeconds;
            _tmpTimeSpentSeconds = _cursor.getInt(_cursorIndexOfTimeSpentSeconds);
            final Date _tmpTimestamp;
            final Long _tmp_4;
            if (_cursor.isNull(_cursorIndexOfTimestamp)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfTimestamp);
            }
            final Date _tmp_5 = __converters.toDate(_tmp_4);
            if (_tmp_5 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.Date', but it was NULL.");
            } else {
              _tmpTimestamp = _tmp_5;
            }
            _item = new GameProgressEntity(_tmpId,_tmpPuzzleId,_tmpUserId,_tmpCurrentGrid,_tmpOriginalGrid,_tmpDifficulty,_tmpIsCompleted,_tmpTimeSpentSeconds,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getProgressByPuzzleAndUser(final int puzzleId, final Integer userId,
      final Continuation<? super GameProgressEntity> $completion) {
    final String _sql = "SELECT * FROM game_progress WHERE puzzleId = ? AND userId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, puzzleId);
    _argIndex = 2;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, userId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<GameProgressEntity>() {
      @Override
      @Nullable
      public GameProgressEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPuzzleId = CursorUtil.getColumnIndexOrThrow(_cursor, "puzzleId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfCurrentGrid = CursorUtil.getColumnIndexOrThrow(_cursor, "currentGrid");
          final int _cursorIndexOfOriginalGrid = CursorUtil.getColumnIndexOrThrow(_cursor, "originalGrid");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfTimeSpentSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "timeSpentSeconds");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final GameProgressEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpPuzzleId;
            _tmpPuzzleId = _cursor.getInt(_cursorIndexOfPuzzleId);
            final Integer _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            }
            final Integer[][] _tmpCurrentGrid;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfCurrentGrid);
            _tmpCurrentGrid = __converters.toSudokuGrid(_tmp);
            final Integer[][] _tmpOriginalGrid;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfOriginalGrid);
            _tmpOriginalGrid = __converters.toSudokuGrid(_tmp_1);
            final SudokuDifficulty _tmpDifficulty;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfDifficulty);
            _tmpDifficulty = __converters.toDifficulty(_tmp_2);
            final boolean _tmpIsCompleted;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_3 != 0;
            final int _tmpTimeSpentSeconds;
            _tmpTimeSpentSeconds = _cursor.getInt(_cursorIndexOfTimeSpentSeconds);
            final Date _tmpTimestamp;
            final Long _tmp_4;
            if (_cursor.isNull(_cursorIndexOfTimestamp)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfTimestamp);
            }
            final Date _tmp_5 = __converters.toDate(_tmp_4);
            if (_tmp_5 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.Date', but it was NULL.");
            } else {
              _tmpTimestamp = _tmp_5;
            }
            _result = new GameProgressEntity(_tmpId,_tmpPuzzleId,_tmpUserId,_tmpCurrentGrid,_tmpOriginalGrid,_tmpDifficulty,_tmpIsCompleted,_tmpTimeSpentSeconds,_tmpTimestamp);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
