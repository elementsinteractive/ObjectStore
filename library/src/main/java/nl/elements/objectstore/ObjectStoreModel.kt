package nl.elements.objectstore

import nl.elements.objectstore.model.AnyItem
import nl.elements.objectstore.model.AnyNullableItem
import kotlin.properties.ReadWriteProperty

abstract class ObjectStoreModel(
    internal val store: ObjectStore
) {
    /**
     * Clears all items in this store
     */
    fun clear() {
        store.keys.forEach(store::remove)
    }

    /**
     * Delegate a string item
     * @param default value
     * @param key custom key (optional)
     */
    protected fun stringItem(
        default: String = "",
        key: String? = null
    ): ReadWriteProperty<ObjectStoreModel, String> = AnyItem(default, key)

    /**
     * Delegate a nullable string item
     * @param default value
     * @param key custom key (optional)
     */
    protected fun nullableStringItem(
        default: String? = null,
        key: String? = null
    ): ReadWriteProperty<ObjectStoreModel, String?> = AnyNullableItem(default, key)

    /**
     * Delegate a boolean item
     * @param default value
     * @param key custom key (optional)
     */
    protected fun booleanItem(
        default: Boolean = false,
        key: String? = null
    ): ReadWriteProperty<ObjectStoreModel, Boolean> = AnyItem(default, key)

    /**
     * Delegate a nullable boolean item
     * @param default value
     * @param key custom key (optional)
     */
    protected fun booleanNullabeItem(
        default: Boolean? = null,
        key: String? = null
    ): ReadWriteProperty<ObjectStoreModel, Boolean?> = AnyNullableItem(default, key)

    /**
     * Delegate an int item
     * @param default value
     * @param key custom key (optional)
     */
    protected fun intItem(
        default: Int = 0,
        key: String? = null
    ): ReadWriteProperty<ObjectStoreModel, Int> = AnyItem(default, key)

    /**
     * Delegate a long item
     * @param default value
     * @param key custom key (optional)
     */
    protected fun longItem(
        default: Long = 0L,
        key: String? = null
    ): ReadWriteProperty<ObjectStoreModel, Long> = AnyItem(default, key)

    /**
     * Delegate a float item
     * @param default value
     * @param key custom key (optional)
     */
    protected fun floatItem(
        default: Float = 0F,
        key: String? = null
    ): ReadWriteProperty<ObjectStoreModel, Float> = AnyItem(default, key)
}
