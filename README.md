[ ![Download](https://api.bintray.com/packages/elementsinteractive/maven/ObjectStore/images/download.svg) ](https://bintray.com/elementsinteractive/maven/ObjectStore/_latestVersion)
# ObjectStore
###### Convenient interface for persisting objects.
``` groovy
compile "nl.elements.objectstore:objectstore:+"
```
- Customizable serialization
- Customizable encryption
- Implementations for `SharedPreferences`, `SqliteDatabase` and `File`.
 
```kotlin
fun example(store: ObjectStore) {
    if ("id" !in store)
        store["id"] = 123L

    val id: Long = store["id"]

    store.remove("id")
}
```

## Observing
Each `ObjectStore` is (Rx) observable and will emit whenever something changes in store. 

```kotlin
fun observe(store: ObjectStore) {
    store
        .toObservable()
        .filter { event -> event.key == "id" }
        .map { event ->
            when (event) {
                is ObjectStore.Event.Updated -> store.get(event.key)
                is ObjectStore.Event.Removed -> -1L
            }
        }
        .subscribe(::println)
}
``` 

## Encrypting
The store can be initialized with a adapter that can transform the incoming bytes into encrypted bytes and vice versa. By default there is no encryption enabled, but there is an implementation based on [Facebook's Conceal](https://github.com/facebook/conceal) included.
```kotlin
fun conceal(context: Context) {
    val keyChain = SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256)
    val crypto = AndroidConceal.get().createDefaultCrypto(keyChain)
    val prefs = context.getSharedPreferences("example", Context.MODE_PRIVATE)
    
    val store = PreferencesStore(
        preferences = prefs,
        transformer = ConcealTransformer(crypto)
    )
}
```

## Aggregating
Each store has its own speciality (big values or a lot of small ones), but that is only interesting when you're writing to a store. When retrieving just want to query all the stores at once:
```kotlin
fun aggregate(directory: File, preferences: SharedPreferences) {
    // define the stores
    val pictures: ObjectStore = DirectoryStore(directory)
    val config: ObjectStore = PreferencesStore(preferences)

    // reduce them into one store
    val stores = listOf(pictures, config)
    val store: ReadableObjectStore = stores.reduce()

    // read from the stores
    val picture: Bitmap = store["selfie"]
    val token: String = store["debug"]
}
```

The above method provides a read-only store, because it can not differentiate to which store it should persist to. If such functionality is desired, you can prefix your store:

```kotlin
fun aggregateWithNamespace(directory: File, preferences: SharedPreferences) {
    // define the stores
    val pictures: ObjectStore = DirectoryStore(directory)
    val config: ObjectStore = PreferencesStore(preferences)

    // reduce them into one store
    val stores = mapOf("pictures" to pictures, "config" to config)
    val store: ReadableObjectStore = stores.reduceWithNamespace()

    // read from the stores
    val picture: Bitmap = store["pictures:selfie"]
    val token: String = store["config:debug"]
}
```
