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

        assertEquals(null, model.nameNull)
        assertEquals(DEFAULT_VALUE, model.nameNullWithDefault)
    }

    companion object {
        class InMemoryModel : ObjectStoreModel(MemoryStore()) {
            var name by stringItem()
            var nameWithDefault by stringItem(DEFAULT_VALUE)

            var nameNull by nullableStringItem()
            var nameNullWithDefault by nullableStringItem(DEFAULT_VALUE)
        }

        const val DEFAULT_VALUE = "default"
    }
}
