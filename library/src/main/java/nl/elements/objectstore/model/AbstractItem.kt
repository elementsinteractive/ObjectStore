package nl.elements.objectstore.model

import nl.elements.objectstore.ObjectStoreModel
import kotlin.properties.ReadWriteProperty

abstract class AbstractItem<T : Any?> : ReadWriteProperty<ObjectStoreModel, T>, ItemKey {
}

abstract class NullableAbstractItem<T : Any?> : ReadWriteProperty<ObjectStoreModel, T?>, ItemKey {
}
