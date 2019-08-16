package nl.elements.objectstore.model

import nl.elements.objectstore.ObjectStoreModel
import kotlin.reflect.KProperty

internal class StringItem(
    val default: String,
    override val key: String?
) : AbstractItem<String>() {

    override fun getValue(model: ObjectStoreModel, property: KProperty<*>): String {
        val realKey = key ?: property.name

        return if (model.store.contains(realKey)) {
            model.store.get(realKey)
        } else {
           default
        }
    }

    override fun setValue(model: ObjectStoreModel, property: KProperty<*>, value: String) {
        model.store[key ?: property.name] = value
    }
}
