package nl.elements.objectstore

interface ReadableObjectStore {

    val keys: Set<String>

    operator fun <T : Any> get(key: String): T

    operator fun contains(key: String): Boolean

}
