package nl.elements.objectstore.android.transformers

import com.facebook.crypto.Crypto
import com.facebook.crypto.Entity
import nl.elements.objectstore.Transformer
import java.io.InputStream
import java.io.OutputStream

class ConcealTransformer(private val crypto: Crypto) : Transformer {

    override fun read(key: String, input: InputStream, output: OutputStream) =
        crypto
            .decrypt(input.readBytes(), Entity.create(key))
            .let(output::write)

    override fun write(key: String, input: InputStream, output: OutputStream) =
        crypto
            .encrypt(input.readBytes(), Entity.create(key))
            .let(output::write)

}
