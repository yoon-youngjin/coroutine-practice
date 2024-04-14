# async와 Deferred

launch 빌더를 통해 생성되는 코루틴은 결과를 반환하지 않는다.
코루틴 라이브러리는 비동기 작업으로부터 결과를 수신해야 하는 경우를 위해 async 코루틴 빌더를 통해 코루틴으로부터 결과값을 수신받을 수 있도록 한다.

async는 Job이 아닌 Deferred를 반환한다.

## async를 사용해 결과값 수신하기

Deferred 객체는 미래의 어느 시점에 결과값이 반환될 수 있음을 표현하는 코루틴 객체이다.
결과값이 필요하다면 결과값이 수신될 때까지 대기해야 한다. 이를 위해 await 함수를 제공한다.

await 함수는 대상이 된 Deferred 코루틴이 실행 완료될 때까지 await 함수를 호출한 코루탄을 일시 중단하며, Deferred 코루틴이 실행 완료되면 결과값을 반환하고 호출한 코루틴을 재개한다.

```kotlin
fun main() = runBlocking {
    val networkDeferred: Deferred<String> = async(Dispatchers.IO) {
        delay(1000L)
        return@async "Dummy Response"
    }

    val result = networkDeferred.await()
    println(result)
}
```

Deferred 객체는 Job 객체의 특수한 형태로 Deferred 인터페이스는 Job 인터페이스의 서브타입으로 선언된 인터페이스다.
즉, Deferred 객체는 Job 의 기능을 모두 사용하고 추가된 기능까지 사용할 수 있다.

## 복수의 코루틴으로부터 결과값 수신하기

```kotlin
suspend fun multiAsyncTest(coroutineScope: CoroutineScope) {
    val startTime = System.currentTimeMillis()
    val participantDeferred1 = coroutineScope.async(Dispatchers.IO) {
        delay(1000)
        return@async arrayOf("James", "Jason")
    }
    val participantDeferred2 = coroutineScope.async(Dispatchers.IO) {
        delay(1000)
        return@async arrayOf("Jenny")
    }

    val participants1 = participantDeferred1.await()
    val participants2 = participantDeferred2.await()

    // 또는 awaitAll -> val results = awaitAll(participantDeferred1, participantDeferred2)
    // 확장함수도 존재 -> val results = listOf(participantDeferred1, participantDeferred2).awaitAll()
    
    println("[${getElapsedTime(startTime)}] 참여자 목록: ${listOf(*participants1, *participants2)}")
}
```

## withContext

withContext를 통해 async-await을 대체할 수 있다.

```kotlin
public suspend fun <T> withContext(
    context: CoroutineContext,
    block: suspend CoroutineScope.() -> T
): T {
    //..
}
```

withContext 함수가 호출되면 인자로 설정된 CoroutineContext 객체를 사용해 block 람다식을 실행하고 완료되면 그 결과를 반환한다.
withContext 함수를 호출한 코루틴은 인자로 받은 CoroutineContext 객체를 사용해 block 람다식을 실행하며, block 람다식을 모두 실행하면 다시 기존의 CoroutineContext 객체를 사용해 코루틴이 재개된다.

```kotlin
suspend fun withContextTest(coroutineScope: CoroutineScope) {
    val result = withContext(Dispatchers.IO) {
        delay(1000)
        return@withContext "Dummy Response"
    }
    println(result)
}
```

async-await 쌍을 연속적으로 실행했을 때와 동작이 매우 비슷하지만 내부 동작은 다르게 동작한다.
async-await 쌍은 새로운 코루틴을 생성해 작업을 처리하지만 withContext 함수는 실행 중이던 코루틴을 그대로 유지한 채로 코루틴의 실행 환경만 변경해 작업을 처리한다.

```kotlin
suspend fun withContextTest2() {
    println("[${Thread.currentThread().name}] runBlocking 블록 실행")
    withContext(Dispatchers.IO) {
        println("[${Thread.currentThread().name}] withContext 블록 실행")
    }
}
```

```text
[main @coroutine#1] runBlocking 블록 실행
[DefaultDispatcher-worker-1 @coroutine#1] withContext 블록 실행
```

결과를 보면 runBlocking 함수의 block 람다식을 실행하는 스레드와 withContext 함수의 block 람다식을 실행하는 스레드는 다르지만
동일한 코루틴(@coroutine#1)인걸 확인할 수 있다. 
즉, withContext 함수는 새로운 코루틴을 만드는 대신 기존의 코루틴에서 CoroutineContext 객체(Dispatcher.IO)만 바꿔서 실행된다.

withContext 함수가 호출되면 실행 중인 코루틴의 실행 환경이 withContext 함수의 context 인자 값으로 변경돼 실행되며,
이를 컨텍스트 스위칭이라 부른다.
따라서 만약 context 인자로 CoroutineDispatcher 객체가 넘어온다면 코루틴은 해당 CoroutineDispatcher 객체를 사용해 다시 실행된다.

위 코드에서는 withContext가 호출되면 인자로 넘어온 Dispatchers.IO 작업 대기열로 이동한 후 Dispatchers.IO의 스레드풀에서 가용할 수 있는 스레드로 실행된다.

> withContext 함수가 block 을 벗어나면 다시 원래의 CoroutineContext 객체를 사용해 실행된다.

```kotlin
suspend fun withContextTest3(coroutineScope: CoroutineScope) {
    println("[${Thread.currentThread().name}] runBlocking 블록 실행")
    coroutineScope.async(Dispatchers.IO) {
        println("[${Thread.currentThread().name}] withContext 블록 실행")
    }.await()
}
```

```text
[main @coroutine#1] runBlocking 블록 실행
[DefaultDispatcher-worker-1 @coroutine#2] withContext 블록 실행
```

반면에 async-await 쌍으로 실행하면 새로운 코루틴을 생성하여 실행하는것을 볼 수 있다.

**정리**
- withContext 는 코루틴이 유지된 채로 코루틴을 실행하는 실행 스레드만 변경되기 때문에 동기적으로 실행되는 것
- async 빌더를 통해 새로운 코루틴을 만들지만 await 함수를 이용해 순차 처리가 돼 동기적으로 실행되는 것

withContext는 코루틴을 공유하므로 앞선 코루틴에 작업이 지연되면 withContext의 block 람다식 실행이 늦어질 수 있을듯

```kotlin
suspend fun withContextTest4() {
    val startTime = System.currentTimeMillis()
    val helloDeferred = withContext(Dispatchers.IO) {
        delay(1000)
        return@withContext "hello"
    }
    val worldDeferred = withContext(Dispatchers.IO) {
        delay(1000)
        return@withContext "world"
    }

    println("[${getElapsedTime(startTime)}] ${helloDeferred}, $worldDeferred")
}
```

```text
[DefaultDispatcher-worker-1 @coroutine#1] helloDeferred 블록 실행
[DefaultDispatcher-worker-1 @coroutine#1] worldDeferred 블록 실행
[지난 시간: 2025ms] hello, world
```

즉, withContext 는 코루틴을 새로 생성하지 않고 재사용하기 때문에 순차적으로 처리될 수는 있으나 병렬적으로 처리되어야 하는 경우에 문제를 야기할 수 있다. 

