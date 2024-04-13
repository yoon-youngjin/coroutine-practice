# CoroutineDispatcher

CoroutineDispatcher 는 코루틴을 스레드로 보내 실행시키는 주체이다. 
- CoroutineDispatcher 는 코루틴을 스레드로 보내는 데 사용할 수 있는 스레드나 스레드풀을 가지며, 코루틴을 실행 요청한 스레드에서 코루틴이 실행되도록 만들 수 있다. 
- CoroutineDispatcher 객체는 실행돼야 하는 작업을 저장하는 작업 대기열을 가진다.
  - 스레드풀에도 작업 대기열이 존재하는데?

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/24167c0d-e25d-4d0e-836d-712535718ed7)

- CoroutineDispatcher 객체는 자신이 사용할 수 있는 스레드가 있는지 확인한다. 
- 위 그림에서는 Thread2 스레드를 사용할 수 있으므로 Coroutine3 코루틴을 해당 스레드로 보내 실행시킨다.
- 만약 모든 스레드에서 작업이 실행중이라면 대기열에 코루틴이 적재되고, 특정 스레드가 자유로워지면 해당 스레드가 코루틴을 가져가서 처리된다.
- 일반적으로 작업 대기열에 적재한 후에 스레드로 보내지만 코루틴의 실행 옵션에 따라 작업 대기열에 적재되지 않고 즉시 실행될 수도 있고, 작업 대기열이 없는 CoroutineDispatcher 구현체도 존재한다.

## CoroutineDispatcher 종류

**Confined Dispatcher / Unconfined Dispatcher**

- 둘의 차이는 사용할 수 있는 스레드나 스레드풀이 제한되었는지 여부
- 무제한 디스패처는 실행 요청된 코루틴이 아무 스레드에서나 실행되는것은 아니다. 호출 스레드에서 코루틴을 시작하지만 이는 첫번째 중단점을 만날때 까지만 그렇다.
  - 중단점 이후에 코루틴이 재개될 때는 중단 함수를 재개한 스레드에서 수행된다. 
  - 이로인해서 실행되는 스레드가 매번 달라질 수 있고, 특정 스레드로 제한돼 있지 않아 무제한 디스패처라는 이름을 갖게 됐다.

### 제한된 디스패처 만들기

- val dispatcher = newSingleThreadContext("SingleThread")
  - 해당 디스패처는 작업 대기열을 가지고 뒷단에는 한개의 스레드를 가진다.
- val multiThreadDispatcher = newFixedThreadPoolContext(20,"MultiThread")
    - 해당 디스패처는 작업 대기열을 가지고 뒷단에는 20개의 스레드를 가진다.
    - 스레드의 이름은 MultiThread-1, MultiThread-2 가 된다.

### 부모 코루틴의 CoroutineDispatcher 사용해 자식 코루틴 실행하기

코루틴은 구조화를 제공해 코루틴 내부에서 새로운 코루틴을 실행할 수 있다.

구조화는 코루틴을 계층 관계로 만드는 것뿐만 아니라 부모 코루틴의 실행 환경을 자식 코루틴에 전달하는 데도 사용된다.
만약 자식 코루틴에 디스패처가 설정되지 않았으면 부모 코루틴의 디스패처를 사용한다.

```kotlin
val multiThreadDispatcher = newFixedThreadPoolContext(
    nThreads = 2,
    name = "MultiThread"
)

fun main() = runBlocking<Unit> {
    println("[${Thread.currentThread().name}] 실행")
    launch(multiThreadDispatcher) {
        println("[${Thread.currentThread().name}] 실행")
        launch {
            println("[${Thread.currentThread().name}] 실행")
        }
        launch {
            println("[${Thread.currentThread().name}] 실행")
        }
    }
}
```

```text
[main @coroutine#1] 실행
[MultiThread-1 @coroutine#2] 실행
[MultiThread-2 @coroutine#3] 실행
[MultiThread-2 @coroutine#4] 실행
```

## 미리 정의된 디스패처

직접 디스패처 객체를 만드는 것은 비효율적으로 동작할 수 있으므로 코루틴 라이브러리는 디스패처 객체를 생성하는 문제를 방지하기 위해 미리 정의된 디스패처 목록을 제공한다.

### Dispatchers.IO

IO 작업에서는 많은 스레드가 필요하다.
이를 위해 코루틴 라이브러리에서는 입출력 작업을 위해 미리 정의된 Dispatcher.IO를 제공한다.
해당 디스패처의 스레드 수는 JVM에서 사용이 가능한 프로세서의 수와 64 중 큰 값으로 설정돼 있다.

### Dispatchers.Default

대용량 데이터를 처리해야 하는 작업처럼 CPU 연산이 필요한 CPU 바운드 작업에 사용 가능한 디스패처이다.
- JVM의 공유 스레드풀을 사용하고 동시 작업 가능한 최대 갯수는 CPU의 코어 수

### limitedParallelism 사용해 Dispatchers.Default 스레드 사용 제한

Dispatchers.Default를 사용해 무겁고 오래 걸리는 연산을 처리하면 특정 연산을 위해 Dispatchers.Default의 모든 스레드가 사용될 수 있다. 
이 경우 해당 연산이 모든 스레드를 사용하는 동안 Dispatchers.Default를 사용하는 다른 연산이 실행되지 못한다.

이의 방지를 위해 코루틴 라이브러리는 일부 스레드만 사용해 특정 연산을 실행할 수 있도록 하는 limitedParallelism 함수를 지원한다.

```kotlin
fun main() = runBlocking<Unit> {
    launch(Dispatchers.Default.limitedParallelism(2)) {
        repeat(10) {
            launch {
                println("[${Thread.currentThread().name}] 코루틴 실행")
            }
        }
    }
}
```

```text
[DefaultDispatcher-worker-1 @coroutine#4] 코루틴 실행
[DefaultDispatcher-worker-2 @coroutine#3] 코루틴 실행
[DefaultDispatcher-worker-1 @coroutine#5] 코루틴 실행
[DefaultDispatcher-worker-2 @coroutine#6] 코루틴 실행
[DefaultDispatcher-worker-1 @coroutine#7] 코루틴 실행
[DefaultDispatcher-worker-2 @coroutine#8] 코루틴 실행
[DefaultDispatcher-worker-1 @coroutine#9] 코루틴 실행
[DefaultDispatcher-worker-2 @coroutine#10] 코루틴 실행
[DefaultDispatcher-worker-1 @coroutine#11] 코루틴 실행
[DefaultDispatcher-worker-2 @coroutine#12] 코루틴 실행
```

추가적으로 Dispatchers.IO의 limitedParallelism 는 조금 다르다.
Default 디스패처는 자신이 가용할 수 있는 스레드 중 일부만을 사용하지만 IO 디스패처는 공유 스레드풀의 스레드로 구성된 새로운 스레드 풀을 만들어내며, 만들어낼 수 있는 스레드에 제한이 있는 IO, Default 디스패처와 달리 스레드의 수를 제한 없이 만들어낼 수 있다.

```kotlin
fun main() = runBlocking<Unit> {
    launch(Dispatchers.IO.limitedParallelism(100)) {
        repeat(200) {
            launch {
                Thread.sleep(100)
                println("[${Thread.currentThread().name}] 코루틴 실행")
            }
        }
    }
}
```

```text
[DefaultDispatcher-worker-6 @coroutine#123] 코루틴 실행
[DefaultDispatcher-worker-56 @coroutine#132] 코루틴 실행
[DefaultDispatcher-worker-29 @coroutine#127] 코루틴 실행
[DefaultDispatcher-worker-3 @coroutine#125] 코루틴 실행
[DefaultDispatcher-worker-16 @coroutine#128] 코루틴 실행
[DefaultDispatcher-worker-36 @coroutine#142] 코루틴 실행
[DefaultDispatcher-worker-32 @coroutine#133] 코루틴 실행
[DefaultDispatcher-worker-51 @coroutine#141] 코루틴 실행
[DefaultDispatcher-worker-48 @coroutine#140] 코루틴 실행
[DefaultDispatcher-worker-21 @coroutine#139] 코루틴 실행
[DefaultDispatcher-worker-12 @coroutine#138] 코루틴 실행
[DefaultDispatcher-worker-7 @coroutine#134] 코루틴 실행
[DefaultDispatcher-worker-28 @coroutine#130] 코루틴 실행
[DefaultDispatcher-worker-4 @coroutine#137] 코루틴 실행
[DefaultDispatcher-worker-15 @coroutine#135] 코루틴 실행
[DefaultDispatcher-worker-39 @coroutine#129] 코루틴 실행
[DefaultDispatcher-worker-38 @coroutine#136] 코루틴 실행
[DefaultDispatcher-worker-52 @coroutine#154] 코루틴 실행
[DefaultDispatcher-worker-8 @coroutine#131] 코루틴 실행
[DefaultDispatcher-worker-24 @coroutine#124] 코루틴 실행
[DefaultDispatcher-worker-54 @coroutine#106] 코루틴 실행
[DefaultDispatcher-worker-31 @coroutine#155] 코루틴 실행
[DefaultDispatcher-worker-76 @coroutine#160] 코루틴 실행
[DefaultDispatcher-worker-69 @coroutine#164] 코루틴 실행
[DefaultDispatcher-worker-26 @coroutine#113] 코루틴 실행
[DefaultDispatcher-worker-14 @coroutine#119] 코루틴 실행
[DefaultDispatcher-worker-27 @coroutine#108] 코루틴 실행
[DefaultDispatcher-worker-70 @coroutine#163] 코루틴 실행
[DefaultDispatcher-worker-78 @coroutine#166] 코루틴 실행
[DefaultDispatcher-worker-42 @coroutine#165] 코루틴 실행
[DefaultDispatcher-worker-59 @coroutine#159] 코루틴 실행
[DefaultDispatcher-worker-43 @coroutine#157] 코루틴 실행
[DefaultDispatcher-worker-68 @coroutine#162] 코루틴 실행
[DefaultDispatcher-worker-53 @coroutine#103] 코루틴 실행
[DefaultDispatcher-worker-35 @coroutine#109] 코루틴 실행
[DefaultDispatcher-worker-19 @coroutine#121] 코루틴 실행
[DefaultDispatcher-worker-9 @coroutine#117] 코루틴 실행
[DefaultDispatcher-worker-55 @coroutine#153] 코루틴 실행
[DefaultDispatcher-worker-23 @coroutine#120] 코루틴 실행
[DefaultDispatcher-worker-44 @coroutine#161] 코루틴 실행
[DefaultDispatcher-worker-45 @coroutine#143] 코루틴 실행
[DefaultDispatcher-worker-13 @coroutine#145] 코루틴 실행
[DefaultDispatcher-worker-41 @coroutine#146] 코루틴 실행
[DefaultDispatcher-worker-47 @coroutine#147] 코루틴 실행
[DefaultDispatcher-worker-57 @coroutine#148] 코루틴 실행
[DefaultDispatcher-worker-80 @coroutine#169] 코루틴 실행
[DefaultDispatcher-worker-46 @coroutine#149] 코루틴 실행
[DefaultDispatcher-worker-49 @coroutine#150] 코루틴 실행
[DefaultDispatcher-worker-22 @coroutine#112] 코루틴 실행
[DefaultDispatcher-worker-34 @coroutine#114] 코루틴 실행
[DefaultDispatcher-worker-17 @coroutine#144] 코루틴 실행
[DefaultDispatcher-worker-11 @coroutine#107] 코루틴 실행
[DefaultDispatcher-worker-79 @coroutine#170] 코루틴 실행
[DefaultDispatcher-worker-5 @coroutine#110] 코루틴 실행
[DefaultDispatcher-worker-2 @coroutine#122] 코루틴 실행
[DefaultDispatcher-worker-40 @coroutine#151] 코루틴 실행
[DefaultDispatcher-worker-71 @coroutine#175] 코루틴 실행
[DefaultDispatcher-worker-62 @coroutine#156] 코루틴 실행
[DefaultDispatcher-worker-60 @coroutine#152] 코루틴 실행
[DefaultDispatcher-worker-10 @coroutine#126] 코루틴 실행
[DefaultDispatcher-worker-61 @coroutine#158] 코루틴 실행
[DefaultDispatcher-worker-64 @coroutine#183] 코루틴 실행
[DefaultDispatcher-worker-83 @coroutine#174] 코루틴 실행
[DefaultDispatcher-worker-91 @coroutine#180] 코루틴 실행
[DefaultDispatcher-worker-100 @coroutine#185] 코루틴 실행
[DefaultDispatcher-worker-85 @coroutine#188] 코루틴 실행
[DefaultDispatcher-worker-87 @coroutine#191] 코루틴 실행
[DefaultDispatcher-worker-63 @coroutine#167] 코루틴 실행
[DefaultDispatcher-worker-72 @coroutine#168] 코루틴 실행
[DefaultDispatcher-worker-97 @coroutine#192] 코루틴 실행
[DefaultDispatcher-worker-77 @coroutine#189] 코루틴 실행
[DefaultDispatcher-worker-74 @coroutine#172] 코루틴 실행
[DefaultDispatcher-worker-105 @coroutine#201] 코루틴 실행
[DefaultDispatcher-worker-98 @coroutine#196] 코루틴 실행
[DefaultDispatcher-worker-65 @coroutine#171] 코루틴 실행
[DefaultDispatcher-worker-101 @coroutine#198] 코루틴 실행
[DefaultDispatcher-worker-75 @coroutine#182] 코루틴 실행
[DefaultDispatcher-worker-99 @coroutine#186] 코루틴 실행
[DefaultDispatcher-worker-67 @coroutine#184] 코루틴 실행
[DefaultDispatcher-worker-58 @coroutine#177] 코루틴 실행
[DefaultDispatcher-worker-94 @coroutine#200] 코루틴 실행
[DefaultDispatcher-worker-81 @coroutine#190] 코루틴 실행
[DefaultDispatcher-worker-92 @coroutine#179] 코루틴 실행
[DefaultDispatcher-worker-1 @coroutine#194] 코루틴 실행
[DefaultDispatcher-worker-50 @coroutine#193] 코루틴 실행
[DefaultDispatcher-worker-88 @coroutine#187] 코루틴 실행
[DefaultDispatcher-worker-86 @coroutine#173] 코루틴 실행
[DefaultDispatcher-worker-90 @coroutine#178] 코루틴 실행
[DefaultDispatcher-worker-82 @coroutine#197] 코루틴 실행
[DefaultDispatcher-worker-73 @coroutine#195] 코루틴 실행
[DefaultDispatcher-worker-93 @coroutine#176] 코루틴 실행
[DefaultDispatcher-worker-96 @coroutine#181] 코루틴 실행
[DefaultDispatcher-worker-95 @coroutine#202] 코루틴 실행
[DefaultDispatcher-worker-84 @coroutine#199] 코루틴 실행
...
```

이를 통해 특정한 작업이 다른 작업에 영향을 받지 않아야 하는 경우 별도 스레드 풀에서 실행되는 것이 필요할 때 사용해볼 수 있다.
- 별도의 스레드 풀을 할당하는 방법도 존재할듯 

Default, IO 디스패처가 공용 스레드풀에서 사용되는 스레드를 제외한 나머지 스레드를 사용 (몇개나존재?)

### 공유 스레드풀을 사용하는 Dispatchers.IO와 Dispatchers.Default

IO와 Default를 사용한 코드 결과를 보면 스레드 이름이 `DefaultDispatcher-worker-`인 것을 볼 수 있다.
이는 두 디스패처가 같은 스레드풀을 사용한다는 것을 의미한다. (코루틴 라이브러리의 공유 스레드풀)

해당 공유 스레드풀에서는 스레드를 무제한으로 생성할 수 있으며, 코루틴 라이브러리는 공유 스레드풀에 스레드를 생성하고 사용할 수 있도록 API를 제공한다.
물론, 스레드풀 내에서 IO와 Default가 사용하는 스레드는 구분된다.

newFixedThreadPoolContext로 만들어지는 디스패처는 자신만 사용할 수 있는 전용 스레드풀을 생성하는 것과 다르게 IO, Default 디스패처는 공유 스레드풀을 사용한다는 것을 기억하자.

### Dispatchers.Main

UI가 있는 애플리케이션에서 메인 스레드의 사용을 위해 사용되는 특별한 디스패처 객체이다.
즉, 코루틴 라이브러리에 대한 의존성만 추가하면 Dispatchers.Main은 사용할 수 없고 별도의 라이브러리인 kotlinx-coroutines-android 를 추가해야 사용할 수 있다.

참조는 가능하지만 모듈이 존재하지 않는다는 오류가 발생한다.

> Dispatchers.Unconfined 도 존재하지만 해당 디스패처는 무제한 디스패처이므로 뒤에서 설명 예정
