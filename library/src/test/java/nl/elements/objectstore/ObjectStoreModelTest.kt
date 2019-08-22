package nl.elements.objectstore

import nl.elements.objectstore.stores.MemoryStore
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ObjectStoreModelTest {

    @Test
    fun `Test Store Model with MemoryStore`() {
        val model = InMemoryModel()

        assertEquals("", model.name)
        assertEquals(DEFAULT_STRING_VALUE, model.nameDefault)

        assertNull(model.nameNull)
        assertEquals(DEFAULT_STRING_VALUE, model.nameNullDefault)

        assertFalse(model.isOpen)
        assertFalse(model.isOpenDefault)

        assertNull(model.isClosedNull)
        assertEquals(DEFAULT_BOOLEAN_VALUE, model.isClosedNullDefault)

        assertEquals(0, model.int)
        assertEquals(DEFAULT_INT_VALUE, model.intDefault)

        assertEquals(0, model.long)
        assertEquals(DEFAULT_LONG_VALUE, model.longDefault)

        assertEquals(0F, model.float)
        assertEquals(DEFAULT_FLOAT_VALUE, model.floatDefault)
    }

    @Test
    fun `Test Store Model with MemoryStore to change values`() {
        val model = InMemoryModel()

        model.isOpen = true
        model.name = "Test"

        assertTrue(model.isOpen)
        assertEquals("Test", model.name)
    }


    companion object {
        class InMemoryModel : ObjectStoreModel(MemoryStore()) {
            var name by stringItem()
            var nameDefault by stringItem(DEFAULT_STRING_VALUE)

            var nameNull by nullableStringItem()
            var nameNullDefault by nullableStringItem(DEFAULT_STRING_VALUE)

            var isOpen by booleanItem()
            var isOpenDefault by booleanItem(false)

            var isClosedNull by booleanNullabeItem()
            var isClosedNullDefault by booleanNullabeItem(DEFAULT_BOOLEAN_VALUE)

            var int by intItem()
            var intDefault by intItem(DEFAULT_INT_VALUE)

            var long by longItem()
            var longDefault by longItem(DEFAULT_LONG_VALUE)

            var float by floatItem()
            var floatDefault by floatItem(DEFAULT_FLOAT_VALUE)
        }

        const val DEFAULT_STRING_VALUE = "default"
        const val DEFAULT_BOOLEAN_VALUE = true
        const val DEFAULT_INT_VALUE = 42
        const val DEFAULT_LONG_VALUE = 42L
        const val DEFAULT_FLOAT_VALUE = 42F
    }
}
