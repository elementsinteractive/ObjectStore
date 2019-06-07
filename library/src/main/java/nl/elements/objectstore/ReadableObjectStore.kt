package nl.elements.objectstore

interface ReadableObjectStore {

    val keys: Set<String>

    operator fun <T : Any> get(key: String): T

    operator fun contains(key: String): Boolean

}

/// Utils for reducing `ReadableObjectStore`

/**
 * Reduces all `ObjectStore`s (in sequence) into one `ObjectStore`.
 */

fun Array<ReadableObjectStore>.reduce() = asSequence().reduce()

/**
 * Reduces all `ObjectStore`s (in sequence) into one `ObjectStore`.
 */

fun Iterable<ReadableObjectStore>.reduce() = asSequence().reduce()

/**
 * Reduces all `ObjectStore`s (in sequence) into one `ObjectStore`.
 */

fun Sequence<ReadableObjectStore>.reduce(): ReadableObjectStore = reduce(::combine)


/// Utility functions

private fun combine(l: ReadableObjectStore, r: ReadableObjectStore): ReadableObjectStore =
    object : ReadableObjectStore {

        override val keys: Set<String>
            get() = l.keys.toMutableSet().apply { addAll(r.keys) }

        override fun <T : Any> get(key: String): T =
            when (key) {
                in l -> l[key]
                else -> r[key]
            }

        override fun contains(key: String): Boolean = key in l || key in r

    }
