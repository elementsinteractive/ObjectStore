package nl.elements.objectstore

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

/**
 * Supplies a basic abstraction for CRUD operations.
 */

interface ObjectStore : ReadableObjectStore, ObservableSource<ObjectStore.Event> {

    val converter: Converter
    val transformer: Transformer

    operator fun set(key: String, value: Any)

    fun remove(key: String)

    fun OutputStream.write(key: String, value: Any) =
        ByteArrayOutputStream()
            .also { converter.serialize(key, value, it) }
            .toByteArray()
            .inputStream()
            .let { transformer.write(key, it, this@write) }

    fun <T : Any> InputStream.read(key: String): T =
        ByteArrayOutputStream()
            .also { transformer.read(key, this@read, it) }
            .toByteArray()
            .inputStream()
            .let { converter.deserialize(key, it) }

    sealed class Event(open val key: String) {
        data class Updated(override val key: String) : Event(key)
        data class Removed(override val key: String) : Event(key)
    }

    abstract class Base : ObjectStore {

        private val subject = PublishSubject.create<Event>()

        final override fun subscribe(observer: Observer<in Event>) = subject.subscribe(observer)

        protected fun emit(event: Event) = subject.onNext(event)
    }
}

fun ObjectStore.toObservable(): Observable<ObjectStore.Event> = Observable.defer { this }

fun ObjectStore.toReadableObjectStore(): ReadableObjectStore = this

fun ObjectStore.writeToBytes(key: String, value: Any): ByteArray =
    ByteArrayOutputStream()
        .also { it.write(key, value) }
        .toByteArray()

// / Utils for reducing `ObjectStore` which prefixes each key with the given `String` and delimiter

private const val DEFAULT_DELIMITER = ":"

/**
 * Reduces all `ObjectStore`s (in sequence) into one store. Each `ObjectStore` his keys get prefixed with the given `Pair.first` and delimiter.
 *
 * Although it is not forbidden, it is not advised to have the `Pair.first` contain the delimiter.
 */

fun Array<Pair<String, ObjectStore>>.reduceWithNamespace(delimiter: String = DEFAULT_DELIMITER) =
    asSequence().reduceWithNamespace(delimiter)

/**
 * Reduces all `ObjectStore`s (in sequence) into one store. Each `ObjectStore` his keys get prefixed with the given `Pair.first` and delimiter.
 *
 * Although it is not forbidden, it is not advised to have the `Pair.first` contain the delimiter.
 */

fun Map<String, ObjectStore>.reduceWithNamespace(delimiter: String = DEFAULT_DELIMITER): ObjectStore =
    toList().asSequence().reduceWithNamespace(delimiter)

/**
 * Reduces all `ObjectStore`s (in sequence) into one store. Each `ObjectStore` his keys get prefixed with the given `Pair.first` and delimiter.
 *
 * Although it is not forbidden, it is not advised to have the `Pair.first` contain the delimiter.
 */

fun Iterable<Pair<String, ObjectStore>>.reduceWithNamespace(delimiter: String = DEFAULT_DELIMITER) =
    asSequence().reduceWithNamespace(delimiter)

/**
 * Reduces all `ObjectStore`s (in sequence) into one store. Each `ObjectStore` his keys get prefixed with the given `Pair.first` and delimiter.
 *
 * Although it is not forbidden, it is not advised to have the `Pair.first` contain the delimiter.
 */

fun Sequence<Pair<String, ObjectStore>>.reduceWithNamespace(delimiter: String = DEFAULT_DELIMITER) =
    fold(unknownNamespaceObjectStore()) { next, (prefix, store) ->
        store.withNamespace("$prefix$delimiter", next)
    }

private fun ObjectStore.withNamespace(namespace: String, next: ObjectStore): ObjectStore =
    object : ObjectStore {

        val store = this@withNamespace

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

private fun unknownNamespaceObjectStore(): ObjectStore = object : ObjectStore {

    override val converter: Converter = Converter.DEFAULT
    override val transformer: Transformer = Transformer.DEFAULT
    override val keys: Set<String> = emptySet()

    override fun set(key: String, value: Any) = throw UnknownNamespaceException(key)

    override fun remove(key: String) = throw UnknownNamespaceException(key)

    override fun <T : Any> get(key: String): T = throw UnknownNamespaceException(key)

    override fun contains(key: String): Boolean = throw UnknownNamespaceException(key)

    override fun subscribe(observer: Observer<in ObjectStore.Event>) {
    }
}

class UnknownNamespaceException internal constructor(key: String) : Exception(key)
