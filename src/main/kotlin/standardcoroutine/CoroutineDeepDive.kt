package standardcoroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.coroutines.resume

@Volatile
var count = 0
val mutex = Mutex()
val reentrantLock = ReentrantLock()
val countChangeDispatcher = newSingleThreadContext("CountChangeThread")
val atomicCount = AtomicInteger(0)

data class Counter(val name: String, val count: Int)

val atomicCounter = AtomicReference(Counter("MyCounter", 0))

fun main() = runBlocking {
//    lockTest1()
//    lockTest2()
//    lockTest3()
//    lockTest4()
//    failedLockTest()
//    coroutineStartDefaultTest(this)
//    coroutineStartAtomicTest(this)
//    coroutineStartUndispatchedTest1(this)
//    coroutineStartUndispatchedTest2(this)
//    unconfinedDispatcherTest1(this)
//    unconfinedDispatcherTest2(this)
//    unconfinedDispatcherTest3(this)
//    unconfinedDispatcherTest4(this)
//    continuationTest1()
//    continuationTest2()
    continuationTest3()
}

suspend fun continuationTest3() {
    println("runBlocking 코루틴 일시 중단 호출")
    val result = suspendCancellableCoroutine { continuation: CancellableContinuation<String> ->
        thread {
            Thread.sleep(1000L)
            continuation.resume("실행 결과")
        }
    }
    println("result: $result")
}

suspend fun continuationTest2() {
    println("runBlocking 코루틴 일시 중단 호출")
    suspendCancellableCoroutine { continuation: CancellableContinuation<Unit> ->
        println("일시 중단 시점의 runBlocking 코루틴 실행 정보: ${continuation.context}")
        continuation.resume(Unit)
    }
    println("일시 중단된 코루틴이 재개 후 실행되는 코드")
}

suspend fun continuationTest1() {
    println("runBlocking 코루틴 일시 중단 호출")
    suspendCancellableCoroutine { continuation: CancellableContinuation<Unit> ->
        println("일시 중단 시점의 runBlocking 코루틴 실행 정보: ${continuation.context}")
    }
    println("일시 중단된 코루틴이 재개되지 않아 실행되지 않는 코드")
}

fun unconfinedDispatcherTest4(coroutineScope: CoroutineScope) {
    coroutineScope.launch(Dispatchers.Unconfined) {
        println("일시 중단 전 실행 스레드: ${Thread.currentThread().name}")
        delay(100L)
        println("일시 중단 후 실행 스레드: ${Thread.currentThread().name}")
    }
}

fun unconfinedDispatcherTest3(coroutineScope: CoroutineScope) {
    println("작업1")
    coroutineScope.launch(Dispatchers.Unconfined) {
        println("작업2")
    }
    println("작업3")
}

fun unconfinedDispatcherTest2(coroutineScope: CoroutineScope) {
    coroutineScope.launch(Dispatchers.IO) {
        launch {
            println("자식 코루틴 실행 스레드: ${Thread.currentThread().name}")
        }
        println("부모 코루틴 실행 스레드: ${Thread.currentThread().name}")
    }
}

fun unconfinedDispatcherTest1(coroutineScope: CoroutineScope) {
    coroutineScope.launch(Dispatchers.Unconfined) {
        println("launch 코루틴 실행 스레드: ${Thread.currentThread().name}")
    }
    println("runBlocking 코루틴 실행 스레드: ${Thread.currentThread().name}")
}

fun coroutineStartUndispatchedTest2(coroutineScope: CoroutineScope) {
    coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
        println("[${Thread.currentThread().name}] 일시 중단 전에는 CoroutineDispatcher을 거치지 않고 즉시 실행")
        delay(100L)
        println("[${Thread.currentThread().name}] 일시 중단 후에는 CoroutineDispatcher을 거쳐 실행")
    }
}

fun coroutineStartUndispatchedTest(coroutineScope: CoroutineScope) {
    coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
        println("작업1")
    }
    println("작업2")
}

fun coroutineStartAtomicTest(coroutineScope: CoroutineScope) {
    val job = coroutineScope.launch(start = CoroutineStart.ATOMIC) {
        println("작업1")
    }
    job.cancel()
    println("작업2")
}

fun coroutineStartDefaultTest(coroutineScope: CoroutineScope) {
    coroutineScope.launch {
        println("작업1")
    }
    println("작업2")
}

suspend fun failedLockTest() {
    withContext(Dispatchers.Default) {
        repeat(10000) {
            launch {
                val currentCount = atomicCount.get()
                atomicCount.set(currentCount + 1)
            }
        }
    }
    println("count = ${atomicCount.get()}")
}


suspend fun lockTest4() {
    withContext(Dispatchers.Default) {
        repeat(10000) {
            launch {
                atomicCounter.getAndUpdate {
                    it.copy(count = it.count + 1)
                }
            }
        }
    }
    println("count = ${atomicCounter.get()}")
}

suspend fun lockTest3() {
    withContext(Dispatchers.Default) {
        repeat(10000) {
            launch {
                atomicCount.getAndUpdate {
                    it + 1
                }
            }
        }
    }
    println("count = $count")
}

suspend fun lockTest2() {
    withContext(Dispatchers.Default) {
        repeat(10000) {
            launch {
                increaseCount()
            }
        }
    }
    println("count = $count")
}

suspend fun increaseCount() = coroutineScope {
    withContext(countChangeDispatcher) {
        count += 1
    }
}

suspend fun lockTest1() {
    withContext(Dispatchers.Default) {
        repeat(10000) {
            launch {
//                reentrantLock.lock()
//                count += 1
//                reentrantLock.unlock()
                mutex.withLock {
                    count += 1
                }
            }
        }
    }
    println("count = $count")
}
