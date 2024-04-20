# runBlocking과 launch의 차이

## runBlocking 함수의 동작 방식

runBlocking 함수가 호출되면 새로운 코루틴인 runBlocking 코루틴이 실행되는데 이 코루틴은 실행이 완료될 때까지 호출부의 스레드를 차단하고 사용한다..

```kotlin
fun main() = runBlocking {
    delay(5000)
    println("[${Thread.currentThread().name}] 코루틴 실행")
}
```

위 로직은 runBlocking 함수가 호출되면 호출 스레드인 메인 스레드를 사용하는 runBlocking 코루틴이 생성된다.
runBlocking 코루틴은 생성 시점부터 실행 완료 시점까지 메인 스레드는 runBlocking 코루틴에 의해 베타적으로 사용되며, 이 코루틴의 실행이 완료될 때까지 다른 작업에 사용될 수 없다.

앞의 코드에서 runBlocking 코루틴은 실행되는 동안 메인 스레드를 점유하고 사용한다. 하지만 runBlocking 코루틴을 호출부의 스레드를 사용하지 않고, 차단만 할 수도 있다.

```kotlin
fun main() = runBlocking(Dispatchers.IO) {
    delay(5000)
    println("[${Thread.currentThread().name}] 코루틴 실행")
}
```

runBlocking 함수가 호출된 스레드와 다른 스레드에서 runBlocking 코루틴이 실행되더라도 해당 코루틴이 실행되는 동안 runBlocking 함수를 호출한 스레드는 차단된다.
차단이 풀리는 시점은 runBlocking 코루틴이 실행 완료될 때다.

runBlocking 함수의 차단은 일반적인 스레드 블로킹과는 다르다.
스레드 블로킹은 스레드가 어떤 작업에도 사용할 수 없도록 차단하는 반면에, runBlocking 함수의 차단은 runBlocking 코루틴과 그 자식 코루틴을 제외한 다른 작업이 스레드를 사용할 수 없음을 의미한다.

```kotlin
fun main() = runBlocking {
    launch(CoroutineName("Coroutine1")) {
        delay(1000)
        println("[${Thread.currentThread().name}] launch 코루틴 종료")
    }
    delay(2000)
    println("[${Thread.currentThread().name}] runBlocking 코루틴 종료")
}
```

- launch 코루틴은 runBlocking 코루틴의 하위 코루틴이므로 메인 스레드를 사용할 수 있다.

## runBlocking 함수와 launch 함수의 동작 차이

runBlocking 코루틴은 runBlocking 함수 호출부의 스레드를 차단하고 사용하지만, launch 함수를 사용해 생성되는 launch 코루틴은 실행될 때 호출부의 스레드를 차단하지 않는다.

```kotlin
fun main() = runBlocking {
    val startTime = System.currentTimeMillis()
    runBlocking(CoroutineName("Coroutine1")) {
        delay(1000)
        println("[${Thread.currentThread().name}] 하위 코루틴 종료")
    }

    println(getElapsedTime(startTime))
}
```

```text
[main] 하위 코루틴 종료
지난 시간: 1016ms
```

바깥쪽 runBlocking 코루틴 하위에 runBlocking 코루틴을 새로 실행한다. 
하위에 생성된 runBlocking 코루틴은 바깥쪽 runBlocking 코루틴이 차단한 스레드를 사용할 수 있기 때문에 메인 스레드상에서 실행되며, 마찬가지로 실행되는 동안 메인 스레드를 차단한다.
따라서 바깥쪽 runBlocking 코루틴은 하위 runBlocking 코루틴이 모두 실행될 때까지 메인 스레드를 사용할 수 없으므로 하위 runBlocking 코루틴이 모두 실행되고 나서야 출력할 수 있다.

> runBlocking은 블로킹을 일으키는 일반적인 코드와 코루틴 사이의 연결점 역할을 하기 위해 만들어졌기 때문에, 코루틴 내부에서 다시 runBlocking을 호출하는 것을 삼가야 한다.

반면에 launch 코루틴은 코루틴 빌더 함수 호출부의 스레드를 차단하지 않는다. 
따라서 delay 같은 작업으로 인해 실제로 스레드를 사용하지 않는 동안 스레드는 다른 작업에 사용될 수 있다.

```kotlin
fun main() = runBlocking {
    val startTime = System.currentTimeMillis()
    launch(CoroutineName("Coroutine1")) {
        delay(1000)
        println("[${Thread.currentThread().name}] 하위 코루틴 종료")
    }

    println(getElapsedTime(startTime))
}
```

```text
지난 시간: 2ms
[main] 하위 코루틴 종료
```

launch 코루틴은 호출부의 스레드를 차단하고 실행되는 것이 아니기 때문에 즉시 실행되지 않고, runBlocking 코루틴이 메인 스레드를 양보하고 나서야 메인 스레드에 보내져 실행된다.
