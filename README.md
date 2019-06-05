# ObjectStore
###### Convenient interface for persisting objects.
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

## Encryption
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