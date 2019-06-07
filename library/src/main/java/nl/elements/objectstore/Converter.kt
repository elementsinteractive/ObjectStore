package nl.elements.objectstore

import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream

/**
 * Used for converting an object into bytes and vice versa.
 */

interface Converter {

    /**
     * Convert the value into bytes that will be used for IO.
     */

    fun serialize(key: String, value: Any, output: OutputStream)

    /**
     * Convert the bytes from IO into the desired object.
     */

    fun <T : Any> deserialize(key: String, input: InputStream): T

    companion object {

        val DEFAULT = object : Converter {

            override fun serialize(key: String, value: Any, output: OutputStream) =
                ObjectOutputStream(output).writeObject(value)

            @Suppress("UNCHECKED_CAST")
            override fun <T : Any> deserialize(key: String, input: InputStream): T =
                ObjectInputStream(input).readObject() as T

        }

    }

}
