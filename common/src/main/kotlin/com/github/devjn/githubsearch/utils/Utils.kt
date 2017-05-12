package com.github.devjn.githubsearch.utils

import java.text.SimpleDateFormat
import java.util.*
import com.github.devjn.githubsearch.model.db.DataBaseField
import com.github.devjn.githubsearch.database.ISQLiteContentValues
import com.github.devjn.githubsearch.database.ISQLiteCursor
import com.github.devjn.githubsearch.database.ISQLiteDatabase


/**
 * Created by @author Jahongir on 30-Apr-17
 * devjn@jn-arts.com
 * Utils
 */
object Utils {

    val TAG = Utils::class.simpleName

    fun <T : Any, R : Any> whenAllNotNull(vararg options: T?, block: (List<T>) -> R) {
        if (options.all { it != null }) {
            block(options.filterNotNull())
        }
    }

    fun <T : Any, R : Any> whenAnyNotNull(vararg options: T?, block: (List<T>) -> R) {
        if (options.any { it != null }) {
            block(options.filterNotNull())
        }
    }

    fun getDateStringInDateFormat(date: Date): String {
        val format = SimpleDateFormat("EEEE, MM/dd/yy kk:mm")
        return format.format(date)
    }

    fun getDateStringInTimeFormat(date: Date): String {
        val format = SimpleDateFormat("kk:mm")
        return format.format(date)
    }

    fun executeSQLStatement(db: ISQLiteDatabase?, statement: String) {
        db!!.execSQL(statement)
    }

    fun createTableSQL(tableName: String, fields: Array<DataBaseField?>): String {
        var statement = "CREATE TABLE IF NOT EXISTS $tableName("
        for (i in fields.indices) {
            statement += fields[i]!!.fieldName + " " + fields[i]!!.fieldType
            if (fields[i]!!.isPrimaryKey) {
                statement += " PRIMARY KEY"
            }
            if (fields[i]!!.isAutoIncrement) {
                statement += " AUTOINCREMENT"
            }
            if (fields[i]!!.isNotNull) {
                statement += " NOT NULL"
            }
            if (i != fields.size - 1) {
                statement += ", "
            }
        }
        statement += ");"
        return statement
    }

    fun createClauseWhereFieldEqualsValue(field: DataBaseField, value: Any): String {
        val clause = field.fieldName + "=" + value
        return clause
    }

    fun dropTableIfExistsSQL(tableName: String): String {
        val statement = "DROP TABLE IF EXISTS $tableName;"
        return statement
    }

    fun insertObject(db: ISQLiteDatabase, tableName: String, values: ISQLiteContentValues) {
        db.insert(tableName, null, values)
    }

    fun deleteObject(db: ISQLiteDatabase, tableName: String, whereClause: String) {
        db.delete(tableName, whereClause, null)
    }

    fun clearObjects(db: ISQLiteDatabase, tableName: String) {
        db.delete(tableName, null, null)
    }

    fun getAllObjects(db: ISQLiteDatabase, tableName: String, fields: Array<DataBaseField>): ISQLiteCursor? {
        return getAllObjectsOrderedByParam(db, tableName, fields, null)
    }

    fun getAllObjectsOrderedByParam(db: ISQLiteDatabase, tableName: String, fields: Array<DataBaseField>, orderedBy: String?): ISQLiteCursor? {
        val fieldNames = arrayOfNulls<String>(fields.size)
        for (i in fields.indices) {
            fieldNames[i] = fields[i].fieldName
        }
        return db.query(tableName, fieldNames, null, null, null, null, orderedBy)
    }

}