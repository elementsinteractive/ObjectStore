package nl.elements.objectstore

import java.io.InputStream
import java.io.OutputStream

/**
 * Used for transforming a stream of bytes into another stream of bytes.
 */

interface Transformer {

    /**
     * Transforms the bytes that are read from IO.
     */

    fun read(key: String, input: InputStream, output: OutputStream)

    /**
     * Transforms the bytes that will be written into IO.
     */

    fun write(key: String, input: InputStream, output: OutputStream)

    companion object {

        val DEFAULT = object : Transformer {

            override fun read(key: String, input: InputStream, output: OutputStream) {
                input.copyTo(output)
            }

            override fun write(key: String, input: InputStream, output: OutputStream) {
                input.copyTo(output)
            }

        }

    }

}

