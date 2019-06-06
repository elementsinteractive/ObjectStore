import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import nl.elements.objectstore.Converter
import nl.elements.objectstore.Transformer
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream


/**
 * Supplies a basic abstraction for CRUD operations.
 */

interface ObjectStore : ObservableSource<ObjectStore.Event> {

    val converter: Converter
    val transformer: Transformer

    val keys: Set<String>

    operator fun <T : Any> get(key: String): T

    operator fun set(key: String, value: Any)

    operator fun contains(key: String): Boolean

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

fun ObjectStore.writeToBytes(key: String, value: Any): ByteArray =
    ByteArrayOutputStream()
        .also { it.write(key, value) }
        .toByteArray()
