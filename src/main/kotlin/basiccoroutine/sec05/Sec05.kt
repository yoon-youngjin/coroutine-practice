package basiccoroutine.sec05

import kotlinx.coroutines.*
import basiccoroutine.printWithThread

fun example1(): Unit = runBlocking {
    val job1 = CoroutineScope(Dispatchers.Default).launch {
        delay(1000)
        basiccoroutine.printWithThread("Job 1")
    }

    val job2 = CoroutineScope(Dispatchers.Default).launch {
        delay(1000)
        basiccoroutine.printWithThread("Job 2")
    }
    job1.join()
    job2.join()
}

fun example2(): Unit = runBlocking {
    val job = launch {
        try {
            throw IllegalArgumentException()
        } catch (e: IllegalArgumentException) {
            basiccoroutine.printWithThread("정상 종료")
        }
    }
}

fun example3() = runBlocking {
    val job = async(SupervisorJob()) {
        throw IllegalArgumentException()
    }
    delay(1000)
    try {
        job.await()
    } catch (e: IllegalArgumentException) {
        basiccoroutine.printWithThread("예외 발생 ${e.message}")
    }
}

fun main() = runBlocking {
    val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
        if (throwable is IllegalArgumentException) {
            basiccoroutine.printWithThread("예외발생 ${throwable.message}")
        } else {
            basiccoroutine.printWithThread("예외")
        }
    }
    val job = CoroutineScope(Dispatchers.Default).async(exceptionHandler) {
        throw IllegalArgumentException()
    }
    job.await()

    delay(1000)
}


