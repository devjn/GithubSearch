package com.github.devjn.githubsearch.database

interface ISQLiteDatabase {

    fun newContentValues(): ISQLiteContentValues

    fun query(table: String?, columns: Array<String?>, selection: String?,
              selectionArgs: Array<String>?, groupBy: String?, having: String?,
              orderBy: String?): ISQLiteCursor?

    fun delete(table: String, whereClause: String?, whereArgs: Array<String>?): Int

    fun insert(table: String, nullColumnHack: String?, values: ISQLiteContentValues): Long

    fun execSQL(statement: String)

    fun update(tableName: String, values: ISQLiteContentValues,
               whereClause: String, whereArgs: Array<String>?) : Int

    fun rawQuery(sql: String, selectionArgs: Array<String>?): ISQLiteCursor?

}