package nl.elements.objectstore.stores

import nl.elements.objectstore.Converter
import nl.elements.objectstore.ObjectStore
import nl.elements.objectstore.ObjectStore.Event.Removed
import nl.elements.objectstore.ObjectStore.Event.Updated
import nl.elements.objectstore.Transformer
import nl.elements.objectstore.writeToBytes

class MemoryStore(
    override val converter: Converter = Converter.DEFAULT,
    override val transformer: Transformer = Transformer.DEFAULT,
    private val lock: Any = Any()
) : ObjectStore.Base() {

    private val data = mutableMapOf<String, ByteArray>()
    override val keys: Set<String>
        get() = synchronized { data.keys.toSet() }

    override fun <T : Any> get(key: String): T =
        synchronized { data[key]!! }
            .inputStream()
            .read(key)

    override fun set(key: String, value: Any) {
        val bytes = writeToBytes(key, value)

        synchronized { data[key] = bytes }
        emit(Updated(key))
    }

    override fun contains(key: String): Boolean = synchronized { key in data }

    override fun remove(key: String) {
        synchronized { data.remove(key) }?.let { emit(Removed(key)) }
    }

    private fun <R> synchronized(block: () -> R): R = synchronized(lock, block)

}
