# CoroutineContext

```kotlin
public fun CoroutineScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job

public fun <T> CoroutineScope.async(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T>
```

launch, async 함수는 매개변수로 context, start, block을 가진다.
context 위치에 CoroutineName, CoroutineDispatcher, .. 등이 사용될 수 있는데, 가능한 이유는 CoroutineContext 객체의 구성요소이기 때문이다.

CoroutineContext는 코루틴을 실행하는 실행 환경을 설정하고 관리하는 인터페이스로 CoroutineContext 객체는 CoroutineDispatcher, CoroutineName, Job 등의 객체를 조합해 코루틴의 실행 환경을 설정한다.
즉, 코루틴 실행과 관련된 모든 설정은 CoroutineContext 객체를 통해 이뤄진다.

## CoroutineContext 구성 요소

CoroutineContext 객체는 CoroutineName, CoroutineDispatcher, Job, CoroutineExceptionHandler 네 가지 중요한 구성 요소를 가진다.
1. CoroutineName: 코루틴 이름을 설정
2. CoroutineDispatcher: 코루틴을 스레드에 할당해 실행
3. Job: 코루틴의 추상체로 코루틴을 조작하는 데 사용
4. CoroutineExceptionHandler: 코루틴에서 발생한 예외 처리

## CoroutineContext 구성하기

CoroutineContext 객체는 키-값 쌍으로 각 구성 요소를 관리한다. (ex. CoroutineName 키 -> CoroutineName 객체)
- 각 키에 대한 중복된 값은 허용되지 않는다. 즉, CoroutineContext 객체는 CoroutineName, CoroutineDispatcher, Job, CoroutineExceptionHandler 객체를 한 개씩만 가질 수 있다.

CoroutineContext 객체는 키-값 쌍으로 구성 요소를 관리하지만 키에 값을 직접 대입하는 방법을 사용하지 않는다.
대신, CoroutineContext 객체 간에 더하기 연산자를 사용해 CoroutineContext 객체를 구성한다.
- ex. CoroutineDispatcher + CoroutineName -> newSingleThreadContext("MyThread") + CoroutineName("MyCoroutine")

```kotlin
val coroutineContext = newSingleThreadContext("myThread") + CoroutineName("myCoroutine")
```

이렇게 구성한 CoroutineContext 객체의 구성요소는 
- CoroutineName 키 -> CoroutineName("myCoroutine")
- CoroutineDispatcher 키 -> newSingleThreadContext("myThread")
- Job 키 -> 설정 X
- CoroutineExceptionHandler 키 -> 설정 X

> 구성 요소가 없는 CoroutineContext는 EmptyCoroutineContext를 통해 만들 수 있다.

만약 CoroutineContext 객체에 같은 구성 요소가 둘 이상 더해진다면 나중에 추가된 CoroutineContext 구성 요소가 이전의 값을 덮어씌운다.

```kotlin
fun overwriteCoroutineContextTest(coroutineScope: CoroutineScope) {
    val coroutineContext = newSingleThreadContext("myThread") + CoroutineName("myCoroutine")

    val newCoroutineContext = coroutineContext + CoroutineName("newCoroutine")
    coroutineScope.launch(newCoroutineContext) {
        println("[${Thread.currentThread().name}] 실행")
    }
}
```

여러 구성 요소로 이뤄진 CoroutineContext 객체 2개가 합쳐지고 2개의 CoroutineContext 객체에 동일한 키를 가진 구성 요소가 있다면 나중에 들어온 값이 선택된다.

```kotlin
fun overwriteCoroutineContextTest2(coroutineScope: CoroutineScope) {
    val coroutineContext1 = newSingleThreadContext("myThread1") + CoroutineName("myCoroutine1")
    val coroutineContext2 = newSingleThreadContext("myThread2") + CoroutineName("myCoroutine2")

    val combinedCoroutineContext = coroutineContext1 + coroutineContext2
    coroutineScope.launch(combinedCoroutineContext) {
        println("[${Thread.currentThread().name}] 실행")
    }
}
```

```text
[myThread2 @myCoroutine2#2] 실행
```

- 만약 coroutineContext1에 Job이 존재하였다면 coroutineContext2 에는 존재하지 않으므로 해당 값은 유지될 것이다.

## CoroutineContext 구성 요소에 접근하기

coroutineContext1에 구성 요소의 키는 CoroutineContext.Key 인터페이스를 구현해 만들 수 잇는데 일반적으로 CoroutineContext 구성 요소는 자신의 내부에 키를 싱글톤 객체로 구현한다.

```kotlin
public data class CoroutineName(
    /**
     * User-defined coroutine name.
     */
    val name: String
) : AbstractCoroutineContextElement(CoroutineName) {
    /**
     * Key for [CoroutineName] instance in the coroutine context.
     */
    public companion object Key : CoroutineContext.Key<CoroutineName>
// ...
}
```

```kotlin
fun accessCoroutineContextByKey() {
    val coroutineContext = CoroutineName("myCoroutine") + Dispatchers.IO
    val nameFromContext = coroutineContext[CoroutineName.Key]
    println(nameFromContext)
}
```

```text
CoroutineName(myCoroutine)
```

CoroutineContext 주요 구성 요소들은 동반 객체로 CoroutineContext.KEY 를 구현하는 Key를 갖고 있기 때문에 Key를 명시적으로 사용하지 않고 구성 요소 자체를 키로 사용할 수 있다.

```kotlin
fun accessCoroutineContextByKey() {
    val coroutineContext = CoroutineName("myCoroutine") + Dispatchers.IO
    val nameFromContext = coroutineContext[CoroutineName]
    println(nameFromContext)
}
```

### 구성 요소의 key 프로퍼티를 사용해 구성 요소 접근

CoroutineContext의 모든 구성 요소 인스턴스들은 key 프로퍼티를 가지며, 이를 사용해 구성 요소에 접근할 수 있다.

```kotlin
fun accessCoroutineContextByKeyProperty() {
    val coroutineName = CoroutineName("myCoroutine")
    val dispatcher = Dispatchers.IO
    val coroutineContext = coroutineName + dispatcher

    println(coroutineContext[coroutineName.key])
    println(coroutineContext[dispatcher.key])
}
```

- 여기서 중요한 점은 구성 요소의 Key 프로퍼티는 동반 객체로 선언된 Key와 동일한 객체를 가리킨다.

## CoroutineContext 구성 요소 제거하기

CoroutineContext 객체는 구성 요소를 제거하기 위한 minusKey 함수를 제공한다.
- 해당 함수는 구성 요소의 키를 인자로 받아 해당 구성 요소를 제거한 CoroutineContext 객체를 반환한다.

```kotlin
fun minusKeyTest() {
    val coroutineName = CoroutineName("myCoroutine")
    val dispatcher = Dispatchers.IO
    val coroutineContext = coroutineName + dispatcher

    val deletedCoroutineContext = coroutineContext.minusKey(CoroutineName)
    println(deletedCoroutineContext[coroutineName.key])
    println(deletedCoroutineContext[dispatcher.key])
}
```

```text
null
Dispatchers.IO
```

> minusKey 함수 사용 시 주의할 점은 minusKey를 호출한 객체는 그대로 유지되고, 구성 요소가 제거된 CoroutineContext 객체를 반환한다는 점이다.

