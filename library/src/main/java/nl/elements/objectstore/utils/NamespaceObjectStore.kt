package nl.elements.objectstore.utils

import io.reactivex.Observable
import io.reactivex.Observer
import nl.elements.objectstore.Converter
import nl.elements.objectstore.ObjectStore
import nl.elements.objectstore.Transformer
import nl.elements.objectstore.toObservable

internal fun ObjectStore.withNamespace(namespace: String, next: ObjectStore): ObjectStore =
    NamespaceStore(this, namespace, next)

internal class NamespaceStore(
    private val store: ObjectStore,
    private val namespace: String,
    private val next: ObjectStore
) : ObjectStore {

    override val converter: Converter = store.converter
    override val transformer: Transformer = store.transformer
    override val keys: Set<String>
        get() = store.keys.map { namespace + it }.toMutableSet().apply { addAll(next.keys) }

    override fun <T : Any> get(key: String): T =
        key.removeNamespace()?.let(store::get) ?: next[key]

    override fun contains(key: String): Boolean =
        key.removeNamespace()?.let(store::contains) ?: next.contains(key)

    override fun set(key: String, value: Any) =
        key.removeNamespace()?.let { store[it] = value } ?: next.set(key, value)

    override fun remove(key: String) = key.removeNamespace()?.let(store::remove) ?: next.remove(key)

    override fun subscribe(observer: Observer<in ObjectStore.Event>) =
        store
            .toObservable()
            .map(::prependNamespace)
            .let { Observable.merge(it, next) }
            .subscribe(observer)

    private fun String.removeNamespace(): String? = removePrefix(namespace).takeIf { it != this@removeNamespace }

    private fun prependNamespace(event: ObjectStore.Event): ObjectStore.Event =
        when (event) {
            is ObjectStore.Event.Updated -> ObjectStore.Event.Updated(namespace + event.key)
            is ObjectStore.Event.Removed -> ObjectStore.Event.Removed(namespace + event.key)
        }

}
