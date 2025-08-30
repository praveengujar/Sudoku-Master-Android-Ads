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
public final class OfflinePuzzleDao_Impl implements OfflinePuzzleDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<OfflinePuzzleEntity> __insertionAdapterOfOfflinePuzzleEntity;

  private final Converters __converters = new Converters();

  private final SharedSQLiteStatement __preparedStmtOfDeleteExpiredPuzzles;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllPuzzles;

  public OfflinePuzzleDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfOfflinePuzzleEntity = new EntityInsertionAdapter<OfflinePuzzleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `offline_puzzles` (`id`,`grid`,`solution`,`difficulty`,`createdAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final OfflinePuzzleEntity entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.fromSudokuGrid(entity.getGrid());
        statement.bindString(2, _tmp);
        final String _tmp_1 = __converters.fromSudokuGrid(entity.getSolution());
        statement.bindString(3, _tmp_1);
        final String _tmp_2 = __converters.fromDifficulty(entity.getDifficulty());
        statement.bindString(4, _tmp_2);
        final Long _tmp_3 = __converters.fromDate(entity.getCreatedAt());
        if (_tmp_3 == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, _tmp_3);
        }
      }
    };
    this.__preparedStmtOfDeleteExpiredPuzzles = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM offline_puzzles WHERE createdAt < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllPuzzles = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM offline_puzzles";
        return _query;
      }
    };
  }

  @Override
  public Object insertPuzzle(final OfflinePuzzleEntity puzzle,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfOfflinePuzzleEntity.insert(puzzle);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteExpiredPuzzles(final Date expireTime,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteExpiredPuzzles.acquire();
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
          __preparedStmtOfDeleteExpiredPuzzles.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllPuzzles(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllPuzzles.acquire();
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
          __preparedStmtOfDeleteAllPuzzles.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getPuzzleByDifficulty(final SudokuDifficulty difficulty,
      final Continuation<? super OfflinePuzzleEntity> $completion) {
    final String _sql = "SELECT * FROM offline_puzzles WHERE difficulty = ? ORDER BY createdAt DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromDifficulty(difficulty);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<OfflinePuzzleEntity>() {
      @Override
      @Nullable
      public OfflinePuzzleEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGrid = CursorUtil.getColumnIndexOrThrow(_cursor, "grid");
          final int _cursorIndexOfSolution = CursorUtil.getColumnIndexOrThrow(_cursor, "solution");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final OfflinePuzzleEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final Integer[][] _tmpGrid;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfGrid);
            _tmpGrid = __converters.toSudokuGrid(_tmp_1);
            final Integer[][] _tmpSolution;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfSolution);
            _tmpSolution = __converters.toSudokuGrid(_tmp_2);
            final SudokuDifficulty _tmpDifficulty;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfDifficulty);
            _tmpDifficulty = __converters.toDifficulty(_tmp_3);
            final Date _tmpCreatedAt;
            final Long _tmp_4;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            final Date _tmp_5 = __converters.toDate(_tmp_4);
            if (_tmp_5 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.Date', but it was NULL.");
            } else {
              _tmpCreatedAt = _tmp_5;
            }
            _result = new OfflinePuzzleEntity(_tmpId,_tmpGrid,_tmpSolution,_tmpDifficulty,_tmpCreatedAt);
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

  @Override
  public Object getPuzzlesByDifficulty(final SudokuDifficulty difficulty,
      final Continuation<? super List<OfflinePuzzleEntity>> $completion) {
    final String _sql = "SELECT * FROM offline_puzzles WHERE difficulty = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromDifficulty(difficulty);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<OfflinePuzzleEntity>>() {
      @Override
      @NonNull
      public List<OfflinePuzzleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGrid = CursorUtil.getColumnIndexOrThrow(_cursor, "grid");
          final int _cursorIndexOfSolution = CursorUtil.getColumnIndexOrThrow(_cursor, "solution");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<OfflinePuzzleEntity> _result = new ArrayList<OfflinePuzzleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final OfflinePuzzleEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final Integer[][] _tmpGrid;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfGrid);
            _tmpGrid = __converters.toSudokuGrid(_tmp_1);
            final Integer[][] _tmpSolution;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfSolution);
            _tmpSolution = __converters.toSudokuGrid(_tmp_2);
            final SudokuDifficulty _tmpDifficulty;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfDifficulty);
            _tmpDifficulty = __converters.toDifficulty(_tmp_3);
            final Date _tmpCreatedAt;
            final Long _tmp_4;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            final Date _tmp_5 = __converters.toDate(_tmp_4);
            if (_tmp_5 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.Date', but it was NULL.");
            } else {
              _tmpCreatedAt = _tmp_5;
            }
            _item = new OfflinePuzzleEntity(_tmpId,_tmpGrid,_tmpSolution,_tmpDifficulty,_tmpCreatedAt);
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
  public Object getPuzzleCount(final SudokuDifficulty difficulty,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM offline_puzzles WHERE difficulty = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromDifficulty(difficulty);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(0);
            _result = _tmp_1;
          } else {
            _result = 0;
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
