package nl.elements.objectstore.android.stores

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns._ID
import nl.elements.objectstore.Converter
import nl.elements.objectstore.ObjectStore
import nl.elements.objectstore.ObjectStore.Event.Removed
import nl.elements.objectstore.ObjectStore.Event.Updated
import nl.elements.objectstore.Transformer
import nl.elements.objectstore.writeToBytes

/**
 * A store that maintains its own table for storing key-value pairs.
 */

class DatabaseStore(
    private val database: SQLiteDatabase,
    private val table: String = "key_value_store",
    override val converter: Converter = Converter.DEFAULT,
    override val transformer: Transformer = Transformer.DEFAULT
) : ObjectStore.Base() {

    override val keys: Set<String>
        get() = queryAllIds().use { cursor ->
            mutableSetOf<String>().apply {
                while (cursor.moveToNext()) {
                    add(cursor.id)
                }
            }
        }

    init {
        val query =
            """
                CREATE TABLE IF NOT EXISTS $table (
                    $_ID STRING PRIMARY KEY UNIQUE,
                    $VALUE BLOB NOT NULL
                )
            """

        database.rawQuery(query, null).close()
    }

    override fun set(key: String, value: Any) {
        val bytes = writeToBytes(key, value)
        val values = ContentValues().apply {
            put(_ID, key)
            put(VALUE, bytes)
        }

        database.insertWithOnConflict(table, _ID, values, SQLiteDatabase.CONFLICT_REPLACE)
        emit(Updated(key))
    }

    override fun <T : Any> get(key: String): T =
        queryById(key, VALUE).use { cursor ->
            cursor
                .takeIf { it.moveToFirst() }
                ?.value
                ?.inputStream()
                ?.read(key)!!
        }

    override fun contains(key: String): Boolean =
        queryById(key).use { it.moveToFirst() }

    override fun remove(key: String) {
        database
            .delete(table, "$_ID = ?", arrayOf(key))
            .takeIf { it > 0 }
            ?.let { emit(Removed(key)) }
    }

    private val Cursor.id get() = getString(getColumnIndex(_ID))
    private val Cursor.value get() = getBlob(getColumnIndex(VALUE))

    private fun queryAllIds() =
        database.query(
            table,
            arrayOf(_ID),
            null,
            null,
            null,
            null,
            null
        )

    private fun queryById(key: String, column: String? = null) =
        database.query(
            table,
            column?.let { arrayOf(column) },
            "$_ID = ?",
            arrayOf(key),
            null,
            null,
            null,
            "1"
        )

    companion object {

        const val VALUE = "value"

    }

}
