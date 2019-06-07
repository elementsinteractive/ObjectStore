package nl.elements.objectstore

import nl.elements.objectstore.Converter.Companion.DEFAULT
import org.junit.Test
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals

class ConverterTest {
    @Test
    fun testConverter() {
        val outputStream = ByteArrayOutputStream()
        val key = "testKey"
        val input = "test"

        DEFAULT.serialize(key, input, outputStream)

        val result = DEFAULT.deserialize<String>(key, outputStream.toByteArray().inputStream())

        assertEquals(input, result)
    }
}
