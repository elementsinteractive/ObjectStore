package nl.elements.objectstore

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import nl.elements.objectstore.stores.PreferencesStore
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PreferencesStoreTest {

    private lateinit var store: PreferencesStore

    @Before
    fun setupTests() {
        store = createStore()
    }
    
    @After
    fun clear() {
        createStore().toPreferences().edit().clear().commit()
    }

    @Test
    fun keyContainsInStore() {
        val key = "contains"
        val name = "Danny"

        store.set(key, name)

        assertTrue(store.contains(key))
    }

    @Test
    fun writeReadString() {
        val value = "Danny"

        val result = store.setAndGet("string", value)

        assertEquals(value, result)
    }

    @Test
    fun writeReadBoolean() {
        val value = true

        val result = store.setAndGet("boolean", value)

        assertEquals(value, result)
    }

    @Test
    fun testWriteReadInt() {
        val value = 1

        val result = store.setAndGet("integer", value)

        assertEquals(value, result)
    }

    @Test
    fun testWriteReadLong() {
        val value = 1L

        val result = store.setAndGet("long", value)

        assertEquals(value, result)
    }

    @Test
    fun testWriteReadFloat() {
        val value = 1f

        val result = store.setAndGet("float", value)

        assertEquals(value, result)
    }

    @Test
    fun testWriteReadStringSet() {
        val value = setOf("1", "2", "3")

        val result = store.setAndGet("stringSet", value)

        assertEquals(value, result)
    }

    @Test
    fun getAllAsMap() {
        val key = "name"
        val value = "Danny"

        store.set(key, value)

        val actual = store.keys

        assertTrue(actual.isNotEmpty())
        assertEquals(1, actual.size)
        assertTrue(actual.contains(key))
    }
}

private fun <T : Any> ObjectStore.setAndGet(key: String, value: T): T {
    set(key, value)
    return this[key]
}

private fun createStore(): PreferencesStore {
    val context = InstrumentationRegistry.getInstrumentation().context
    val preferences = context.getSharedPreferences("test", Context.MODE_PRIVATE)

    return PreferencesStore(preferences)
}
