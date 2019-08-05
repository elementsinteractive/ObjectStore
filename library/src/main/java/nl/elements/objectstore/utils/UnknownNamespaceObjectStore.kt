package nl.elements.objectstore.utils

import io.reactivex.Observer
import nl.elements.objectstore.Converter
import nl.elements.objectstore.ObjectStore
import nl.elements.objectstore.Transformer


internal val unknownNamespaceObjectStore: ObjectStore = UnknownNamespaceObjectStore

internal object UnknownNamespaceObjectStore : ObjectStore {

    override val converter: Converter = Converter.DEFAULT
    override val transformer: Transformer =
        Transformer.DEFAULT
    override val keys: Set<String> = emptySet()

    override fun set(key: String, value: Any) = throw UnknownNamespaceException(key)

    override fun remove(key: String) = throw UnknownNamespaceException(key)

    override fun <T : Any> get(key: String): T = throw UnknownNamespaceException(key)

    override fun contains(key: String): Boolean = throw UnknownNamespaceException(key)

    override fun subscribe(observer: Observer<in ObjectStore.Event>) {
    }

}

class UnknownNamespaceException internal constructor(key: String) : Exception(key)