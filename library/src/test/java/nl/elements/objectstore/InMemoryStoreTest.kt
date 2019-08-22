package nl.elements.objectstore

import nl.elements.objectstore.stores.MemoryStore
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InMemoryStoreTest {

    private lateinit var store: MemoryStore

    @Before
    fun setupTests() {
        store = createStore()
    }
    
    @Test
    fun `Store contains given key`() {
        
        val key = "key"
        val value = "Danny"

        store[key] = value

        assertTrue(store.contains(key))
    }

    @Test
    fun `Write and read a String`() {
        val value = "Danny"

        val result = store.setAndGet("string", value)

        assertEquals(value, result)
    }

    @Test
    fun `Write and read a Boolean`() {
        val value = true

        val result = store.setAndGet("boolean", value)

        assertEquals(value, result)
    }

    @Test
    fun `Write and read an Integer`() {
        val value = 1

        val result = store.setAndGet("integer", value)

        assertEquals(value, result)
    }

    @Test
    fun `Write and read a Long`() {
        val value = 1L

        val result = store.setAndGet("long", value)

        assertEquals(value, result)
    }

    @Test
    fun `Write and read a Float`() {
        val value = 1f

        val result = store.setAndGet("float", value)

        assertEquals(value, result)
    }

    @Test
    fun `Write and read a String Set`() {
        val value = setOf("1", "2", "3")

        val result = store.setAndGet("stringSet", value)

        assertEquals(value, result)
    }

    @Test
    fun `Get all objects as map`() {
        val key = "name"
        val value = "Danny"

        store[key] = value

        val actual = store.keys

        assertTrue(actual.isNotEmpty())
        assertEquals(1, actual.size)
        assertTrue(actual.contains(key))
    }
}

private fun <T : Any> ObjectStore.setAndGet(key: String, value: T): T? {
    set(key, value)
    return this[key]
}

private fun createStore() = MemoryStore()

