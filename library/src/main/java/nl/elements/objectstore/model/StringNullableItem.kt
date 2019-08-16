package nl.elements.objectstore.model

import nl.elements.objectstore.ObjectStoreModel
import kotlin.reflect.KProperty

internal class StringNullableItem(
    val default: String?,
    override val key: String?
) : AbstractItem<String?>() {

    override fun getValue(thisRef: ObjectStoreModel, property: KProperty<*>): String? {
        val realKey = key ?: property.name

        return if (thisRef.store.contains(realKey)) {
            thisRef.store.get(realKey)
        } else {
            default
        }
    }

    override fun setValue(thisRef: ObjectStoreModel, property: KProperty<*>, value: String?) {
        thisRef.store[key ?: property.name] = value
    }
}
