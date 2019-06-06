package nl.elements.objectstore

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import nl.elements.objectstore.stores.MemoryStore

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        val context = InstrumentationRegistry.getTargetContext()
        val store = MemoryStore()

        store["key"] = "value"

        val value: String = store["key"]

        assertEquals("value", value)
    }
}
