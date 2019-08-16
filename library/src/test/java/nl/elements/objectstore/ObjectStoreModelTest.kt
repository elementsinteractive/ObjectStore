package nl.elements.objectstore

import nl.elements.objectstore.stores.MemoryStore
import org.junit.Test
import kotlin.test.assertEquals

class ObjectStoreModelTest {

    @Test
    fun `Test Store Model with MemoryStore`() {
        val model = InMemoryModel()

        val newValue = "Test"

        assertEquals("", model.name)

        model.name = newValue

        assertEquals(newValue, model.name)
        assertEquals(DEFAULT_VALUE, model.nameWithDefault)
    }

    companion object {
        class InMemoryModel : ObjectStoreModel(MemoryStore()) {
            var name by stringPref()
            var nameWithDefault by stringPref(DEFAULT_VALUE)
        }

        const val DEFAULT_VALUE = "default"
    }
}
