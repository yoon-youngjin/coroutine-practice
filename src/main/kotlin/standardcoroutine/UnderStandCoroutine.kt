package standardcoroutine

import kotlinx.coroutines.*

fun main() = runBlocking {
//    coroutineTest(this)
//    delayTest(this)
//    noYieldUsingSleepTest(this)
//    yieldWithJoinTest(this)
//    yieldTest(this)
//    allocateToNewThreadTest(this)
    notAssignedToNewThreadTest(this)
}

fun notAssignedToNewThreadTest(coroutineScope: CoroutineScope) {
    val dispatcher = newFixedThreadPoolContext(2, "MyThread")
    coroutineScope.launch(dispatcher) {
        repeat(5) {
            println("[${Thread.currentThread().name}] 코루틴 실행이 일시 중단됩니다")
            Thread.sleep(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행이 재개됩니다")
        }
    }
}

fun allocateToNewThreadTest(coroutineScope: CoroutineScope) {
    val dispatcher = newFixedThreadPoolContext(2, "MyThread")
    coroutineScope.launch(dispatcher) {
        repeat(5) {
            println("[${Thread.currentThread().name}] 코루틴 실행이 일시 중단됩니다")
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행이 재개됩니다")
        }
    }
}

suspend fun yieldTest(coroutineScope: CoroutineScope) {
    val job = coroutineScope.launch {
        while (true) {
            println("작업 중...")
            yield()
        }
    }
    delay(100)
    job.cancel()
}

suspend fun yieldWithJoinTest(coroutineScope: CoroutineScope) {
    val job = coroutineScope.launch {
        println("1. launch 코루틴 자업이 시작됐습니다")
        delay(1000)
        println("2. launch 코루틴 작업이 완료됐습니다")
    }
    println("3. runBlocking 코루틴이 곧 일시 중단되고 메인 스레드가 양보됩니다")
    job.join()
    println("4. runBlocking 코루틴이 메인 스레드에 분배돼 작업이 다시 재개됩니다")
}

fun noYieldUsingSleepTest(coroutineScope: CoroutineScope) {
    val startTime = System.currentTimeMillis()
    repeat(10) { repeatTime ->
        coroutineScope.launch {
            Thread.sleep(1000)
            println("[${getElapsedTime(startTime)}] 코루틴$repeatTime 실행 완료")
        }
    }
}

fun delayTest(coroutineScope: CoroutineScope) {
    val startTime = System.currentTimeMillis()
    repeat(10) { repeatTime ->
        coroutineScope.launch {
            delay(1000)
            println("[${getElapsedTime(startTime)}] 코루틴$repeatTime 실행 완료")
        }
    }
}

private suspend fun coroutineTest(coroutineScope: CoroutineScope) {
    coroutineScope.launch {
        while (true) {
            println("자식 코루틴에서 작업 실행 중")
            yield() // 스레드 사용 권한 양보
        }
    }
    while (true) {
        println("부모 코루틴에서 작업 실행 중")
        yield() // 스레드 권한 양보
    }
}