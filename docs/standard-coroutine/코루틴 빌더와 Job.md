# 코루틴 빌더와 Job

앞서 다룬 runBlocking, launch 는 코루틴을 생성하는 데 사용하는 함수인 코루틴 빌더 함수이다.
모든 코루틴 빌더 함수는 코루틴을 만들고 코루틴을 추상화한 Job 객체를 반환한다.

Job 객체를 통해 코루틴의 상태를 추적하고 제어하는 데 사용할 수 있다.
코루틴은 일시 중단할 수 있는 작업으로 실행 도중 일시 중단된 후 나중에 이어서 실행될 수 있다. 
코루틴을 추상화한 Job 객체는 이에 대응해 코루틴을 제어할 수 있는 함수와 코루틴의 상태 값들을 외부에 노출한다.

## join을 사용한 코루틴 순차 처리

Job 객체는 순차 처리가 필요한 상황을 위해 join 함수를 제공해 먼저 처리돼야 하는 코루틴의 실행이 완료될 때까지 호출부의 코루틴을 일시 중단하도록 만들 수 있다.

Job 객체의 join을 활용하면 join 대상이 된 코루틴의 작업이 완료될 때까지 join을 호출한 코루틴(ex. runBlocking 코루틴)이 일시 중단된다.

```kotlin
fun main() = runBlocking {
    val updateTokenJob = launch(Dispatchers.IO) {
        println("[${Thread.currentThread().name}] 토큰 업데이트 시작")
        delay(100)
        println("[${Thread.currentThread().name}] 토큰 업데이트 완료")
    }
    updateTokenJob.join()

    val networkCallJob = launch(Dispatchers.IO) {
        println("[${Thread.currentThread().name}] 네트워크 요청")
    }
}
```

## joinAll을 사용한 코루틴 순차 처리

복수의 코루틴의 실행이 끝날 때까지 호출부의 코루틴을 일시 중단시키는 joinAll 함수도 제공한다.

```kotlin
suspend fun joinAllTest(coroutineScope: CoroutineScope) {
    val convertImageJob1 = coroutineScope.launch(Dispatchers.Default) {
        delay(1000)
        println("[${Thread.currentThread().name}] 이미지1 변환 완료")
    }

    val convertImageJob2 = coroutineScope.launch(Dispatchers.Default) {
        delay(1000)
        println("[${Thread.currentThread().name}] 이미지2 변환 완료")
    }

    joinAll(convertImageJob1, convertImageJob2)

    coroutineScope.launch(Dispatchers.IO) {
        println("[${Thread.currentThread().name}] 이미지1, 이미지2 업로드")
    }
}
```

## CoroutineStart.LAZY 사용해 코루틴 지연 시작하기

launch 코루틴 빌더를 사용해 코루틴을 생성하면 사용할 수 있는 스레드가 있는 경우 곧바로 시작된다.
하지만 나중에 실행돼야 할 코루틴을 미리 생성해야 할 수 있다. 이런 경우를 위해 코루틴 라이브러리는 생성된 코루틴을 지연 시작할 수 있는 기능을 제공한다.

먼저 코루틴을 생성해두고 나중에 실행해야 하는 경우 LAZY 기능을 사용해볼 수 있다.
코루틴 라이브러리에서 코루틴에 대한 지연 시작 기능을 제공하는데 지연 시작이 저용된 코루틴은 생성 후 대기 상태에 놓이며, 실행을 요청하지 않으면 시작되지 않는다.
이를 위해서는 launch 함수의 start 인자로 CoroutineStart.LAZY 를 넘겨 코루틴에 지연 시작 옵션을 적용해야 한다.

```kotlin
suspend fun lazyCoroutineTest(coroutineScope: CoroutineScope) {
    val startTime = System.currentTimeMillis()
    val lazyJob = coroutineScope.launch(start = CoroutineStart.LAZY) {
        println("[${Thread.currentThread().name}] [${getElapsedTime(startTime)}] 지연 실행")
    }

    delay(1000)
    lazyJob.start()
}
```

## 코루틴 취소하기

코루틴 실행될 필요가 없어졌음에도 취소하지 않고 계속해서 실행되도록 두면 코루틴은 계속해서 스레드를 사용하며 이는 애플리케이션의 성능 저하로 이어진다.

```kotlin
suspend fun cancelJobTest(coroutineScope: CoroutineScope) {
    val startTime = System.currentTimeMillis()
    val longJob = coroutineScope.launch(Dispatchers.Default) {
        repeat(10) { repeatTime ->
            delay(1000)
            println("[${getElapsedTime(startTime)}] 반복횟수 $repeatTime")
        }
    }

    delay(3500)
    longJob.cancel()
}
```

### cancelAndJoin 사용한 순차 처리 

cancel 함수를 호출하면 코루틴은 즉시 취소되는 것이 아니라 Job 객체 내부의 취소 확인용 플래그를 '취소 요청됨'으로 변경함으로써 코루틴이 취소돼야 한다는 것만 알린다.
이후 미래의 어느 시점에 코루틴의 취소가 요청됐는지 체크하고 실질적으로 취소된다.
즉, 특정 코루틴이 cancel이 된 이후에 다른 코루틴을 실행하고 싶다면 단순 cancel 함수로는 순차성을 제어하는데 제약이 존재한다.

이러한 문제를 위해 cancelAndJoin 함수를 제공한다.
코루틴의 취소가 완료될 때까지 호출부의 코루틴이 일시중단된다.

## 코루틴의 취소 확인

cancel, cancelAndJoin을 호출한다고 해서 코루틴이 즉시 취소되는 것이 아니다.
내부 플래그만 변경하고 이후에 해당 플래그를 확인하는 시점에 취소되는 개념이다.

**그럼 플래그를 확인하는 시점(취소를 확인하는 시점)은 언제일까?**

일시 중단 지점이나 코루틴이 실행을 대기하는 시점이며, 이 시점들이 없다면 코루틴은 취소되지 않는다.

```kotlin
suspend fun cancelCheckTest(coroutineScope: CoroutineScope) {
    val whileJob = coroutineScope.launch(Dispatchers.Default) { 
        while (true) {
            println("작업 중...")
        }
    }
    delay(100)
    whileJob.cancel()
}
```

위 함수는 취소 요청을 보내더라도 취소를 확인하는 시점이 존재하지 않으므로 취소되지 않는다.
위 코드를 취소되도록 만드는 3가지 방법이 존재한다.

1. delay
2. yield
3. CoroutineScope.isActive

### delay

```kotlin
suspend fun cancelCheckSuccessTest(coroutineScope: CoroutineScope) {
    val whileJob = coroutineScope.launch(Dispatchers.Default) {
        while (true) {
            delay(1)
            println("작업 중...")
        }
    }
    delay(100)
    whileJob.cancel()
}
```

- 위 코드는 작업을 강제로 1밀리초 동안 일시 중단 시킨다는점에서 효과적이지 않다.

### yield

yield 를 호출하면 코루틴은 자신이 사용하던 스레드를 양보한다. 스레드 사용을 양보한다는 것은 스레드 사용을 중단한다는 뜻이므로 yield를 호출한 코루틴이 일시 중단되며 이 시점에 취소가됐는지 체크가 일어난다.

```kotlin
suspend fun cancelCheckSuccessTest(coroutineScope: CoroutineScope) {
    val whileJob = coroutineScope.launch(Dispatchers.Default) {
        while (true) {
            println("작업 중...")
            yield()
        }
    }
    delay(100)
    whileJob.cancel()
}
```

- 위 코드에서도 while 문을 한 번 돌때마다 스레드 사용이 양보되면서 일시 중단되는 문제가 존재한다.
- 코루틴이 아무리 경량 스레드라고 하더라도 매번 일시 중단되는 것은 작업을 비효율적으로 만든다.

### CoroutineScope.isActive

CoroutineScope는 코루틴이 활성화됐는지 확인할 수 있는 Boolean 타입의 프로퍼티인 isActive를 제공한다.
코루틴에 취소가 요청되면 isActive 프로퍼티의 값은 false로 바뀌며, while 문의 인자로 this.isActive를 넘김으로써 취소 요청되면, while 문을 취소되도록 만들 수 있다.

```kotlin
suspend fun cancelCheckSuccessTest(coroutineScope: CoroutineScope) {
    val whileJob = coroutineScope.launch(Dispatchers.Default) {
        while (this.isActive) {
            println("작업 중...")
        }
    }

    delay(100)
    whileJob.cancel()
}
```

- 코루틴에 별도의 일시 중단 지점없이 명시적으로 코루틴이 취소됐는지 확인하는 코드를 넣어줌으로써 코드를 취소할 수 있도록 만들 수 있다.

## 코루틴의 상태

생성, 실행 중, 실행 완료, 취소 중, 취소 완료 상태를 가질 수 있으며, Job 객체는 코루틴이 어떤 상태에 있는지 나타내는 상태 변수들을 외부에 고액한다.
다만, Job 객체는 코루틴을 추상화한 객체이므로 노출하는 상태 변수들은 코루틴의 상태를 간접적으로만 나타낸다. 
- isActive: 코루틴이 활성화 상태인지 여부
- isCancelled: 코루틴이 취소 요청됐는지 여부, 취소 요청만 받으면 true 이므로 취소 완료되었는지는 판단할 수 없다. 
- isCompleted: 코루틴 실행이 완료됐는지 여부, 모든 코드가 실행 완료되거나 취소 완료되면 true를 반환