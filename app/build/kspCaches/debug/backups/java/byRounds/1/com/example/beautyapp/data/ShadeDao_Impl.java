package com.example.beautyapp.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ShadeDao_Impl implements ShadeDao {
  private final RoomDatabase __db;

  public ShadeDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
  }

  @Override
  public List<Shade> getAllShades() {
    final String _sql = "SELECT * FROM shades";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfShadeId = CursorUtil.getColumnIndexOrThrow(_cursor, "shade_id");
      final int _cursorIndexOfHexCode = CursorUtil.getColumnIndexOrThrow(_cursor, "hex_code");
      final int _cursorIndexOfUndertone = CursorUtil.getColumnIndexOrThrow(_cursor, "undertone");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final List<Shade> _result = new ArrayList<Shade>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Shade _item;
        final int _tmpShadeId;
        _tmpShadeId = _cursor.getInt(_cursorIndexOfShadeId);
        final String _tmpHexCode;
        _tmpHexCode = _cursor.getString(_cursorIndexOfHexCode);
        final String _tmpUndertone;
        if (_cursor.isNull(_cursorIndexOfUndertone)) {
          _tmpUndertone = null;
        } else {
          _tmpUndertone = _cursor.getString(_cursorIndexOfUndertone);
        }
        final String _tmpDescription;
        if (_cursor.isNull(_cursorIndexOfDescription)) {
          _tmpDescription = null;
        } else {
          _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
        }
        _item = new Shade(_tmpShadeId,_tmpHexCode,_tmpUndertone,_tmpDescription);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Shade getShadeById(final int id) {
    final String _sql = "SELECT * FROM shades WHERE shade_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfShadeId = CursorUtil.getColumnIndexOrThrow(_cursor, "shade_id");
      final int _cursorIndexOfHexCode = CursorUtil.getColumnIndexOrThrow(_cursor, "hex_code");
      final int _cursorIndexOfUndertone = CursorUtil.getColumnIndexOrThrow(_cursor, "undertone");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final Shade _result;
      if (_cursor.moveToFirst()) {
        final int _tmpShadeId;
        _tmpShadeId = _cursor.getInt(_cursorIndexOfShadeId);
        final String _tmpHexCode;
        _tmpHexCode = _cursor.getString(_cursorIndexOfHexCode);
        final String _tmpUndertone;
        if (_cursor.isNull(_cursorIndexOfUndertone)) {
          _tmpUndertone = null;
        } else {
          _tmpUndertone = _cursor.getString(_cursorIndexOfUndertone);
        }
        final String _tmpDescription;
        if (_cursor.isNull(_cursorIndexOfDescription)) {
          _tmpDescription = null;
        } else {
          _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
        }
        _result = new Shade(_tmpShadeId,_tmpHexCode,_tmpUndertone,_tmpDescription);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
