package nl.elements.objectstore.stores

import nl.elements.objectstore.Converter
import nl.elements.objectstore.ObjectStore
import nl.elements.objectstore.ObjectStore.Event.Removed
import nl.elements.objectstore.ObjectStore.Event.Updated
import nl.elements.objectstore.Transformer
import java.io.File

/**
 * A store based on a directory. Each key gets its own file in which the value is stored.
 */

class DirectoryStore(
    private val directory: File,
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
            .write(key, value)

        emit(Updated(key))
    }

    override fun <T : Any> get(key: String): T =
        fileOf(key)
            .ensure()
            .inputStream()
            .read(key)

    override fun contains(key: String): Boolean =
        directory
            .list { _, name -> name == key }
            .isNotEmpty()

    override fun remove(key: String) {
        if (fileOf(key).delete())
            emit(Removed(key))
    }

    private fun fileOf(key: String) = File(directory, key)

    private fun File.ensure(): File = apply { createNewFile() }

}
