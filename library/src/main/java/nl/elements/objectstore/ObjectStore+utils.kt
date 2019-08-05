package nl.elements.objectstore

import nl.elements.objectstore.utils.unknownNamespaceObjectStore
import nl.elements.objectstore.utils.withNamespace

private const val DEFAULT_DELIMITER = ":"

/**
 * Reduces all `ObjectStore`s (in sequence) into one store. Each `ObjectStore` his keys get prefixed with the given `Pair.first` and delimiter.
 *
 * Although it is not forbidden, it is not advised to have the `Pair.first` contain the delimiter.
 */

fun Array<Pair<String, ObjectStore>>.reduceWithNamespace(delimiter: String = DEFAULT_DELIMITER) =
    asSequence().reduceWithNamespace(delimiter)

/**
 * Reduces all `ObjectStore`s (in sequence) into one store. Each `ObjectStore` his keys get prefixed with the given `Pair.first` and delimiter.
 *
 * Although it is not forbidden, it is not advised to have the `Pair.first` contain the delimiter.
 */

fun Map<String, ObjectStore>.reduceWithNamespace(delimiter: String = DEFAULT_DELIMITER): ObjectStore =
    toList().asSequence().reduceWithNamespace(delimiter)

/**
 * Reduces all `ObjectStore`s (in sequence) into one store. Each `ObjectStore` his keys get prefixed with the given `Pair.first` and delimiter.
 *
 * Although it is not forbidden, it is not advised to have the `Pair.first` contain the delimiter.
 */

fun Iterable<Pair<String, ObjectStore>>.reduceWithNamespace(delimiter: String = DEFAULT_DELIMITER) =
    asSequence().reduceWithNamespace(delimiter)

/**
 * Reduces all `ObjectStore`s (in sequence) into one store. Each `ObjectStore` his keys get prefixed with the given `Pair.first` and delimiter.
 *
 * Although it is not forbidden, it is not advised to have the `Pair.first` contain the delimiter.
 */

fun Sequence<Pair<String, ObjectStore>>.reduceWithNamespace(delimiter: String = DEFAULT_DELIMITER) =
    fold(unknownNamespaceObjectStore) { next, (prefix, store) ->
        store.withNamespace("$prefix$delimiter", next)
    }
