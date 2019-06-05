import android.content.Context
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import nl.elements.objectstore.Converter
import nl.elements.objectstore.Transformer
import nl.elements.objectstore.stores.PreferencesStore
import nl.elements.objectstore.transformers.ConcealTransformer
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import com.facebook.android.crypto.keychain.AndroidConceal
import com.facebook.crypto.CryptoConfig
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain


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

fun ObjectStore.write(key: String, value: Any, output: OutputStream) =
    ByteArrayOutputStream()
        .also { converter.serialize(key, value, it) }
        .toByteArray()
        .inputStream()
        .let { transformer.write(key, it, output) }

fun ObjectStore.writeToBytes(key: String, value: Any): ByteArray =
    ByteArrayOutputStream()
        .also { write(key, value, it) }
        .toByteArray()

fun <T : Any> ObjectStore.read(key: String, input: InputStream): T =
    ByteArrayOutputStream()
        .also { transformer.read(key, input, it) }
        .toByteArray()
        .inputStream()
        .let { converter.deserialize(key, it) }
