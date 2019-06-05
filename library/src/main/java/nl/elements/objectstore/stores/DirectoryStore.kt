package nl.elements.objectstore.stores

import ObjectStore
import ObjectStore.Event.Removed
import ObjectStore.Event.Updated
import nl.elements.objectstore.Converter
import nl.elements.objectstore.Transformer
import read
import write
import java.io.File

/**
 * A store based on a directory. Each key gets its own file in which the value is stored.
 */

class DirectoryStore(
    val directory: File,
    override val converter: Converter = Converter.DEFAULT,
    override val transformer: Transformer = Transformer.DEFAULT
) : ObjectStore.Base() {

    init {
        assert(directory.isDirectory)
    }

    override val keys: Set<String>
        get() = directory.list().toSet()

    override fun set(key: String, value: Any) {
        fileOf(key)
            .ensure()
            .outputStream()
            .use { write(key, value, it) }

        emit(Updated(key))
    }

    override fun <T : Any> get(key: String): T =
        fileOf(key)
            .ensure()
            .inputStream()
            .use { read(key, it) }

    override fun contains(key: String): Boolean =
        directory
            .list { _, name -> name == key }
            .isNotEmpty()

    override fun remove(key: String) {
        fileOf(key).run {
            if (exists()) {
                delete()
                emit(Removed(key))
            }
        }
    }

    private fun fileOf(key: String) =
        File(directory, key)

    private fun File.ensure(): File =
        apply {
            if (!exists())
                createNewFile()
        }

}
