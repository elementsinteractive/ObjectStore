package nl.elements.objectstore.model

import nl.elements.objectstore.ObjectStoreModel
import kotlin.reflect.KProperty

internal class AnyNullableItem<T : Any>(
    val default: T?,
    override val key: String?
) : NullableAbstractItem<T>() {

    override fun getValue(thisRef: ObjectStoreModel, property: KProperty<*>): T? =
        (key ?: property.name).let { key ->
            if (thisRef.store.contains(key)) {
                thisRef.store[key] ?: default
            } else {
                default
            }
        }

    override fun setValue(thisRef: ObjectStoreModel, property: KProperty<*>, value: T?) {
        thisRef.store[key ?: property.name] = value
    }
}
