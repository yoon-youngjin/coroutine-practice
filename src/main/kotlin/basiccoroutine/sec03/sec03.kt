package basiccoroutine.sec03

import kotlinx.coroutines.*
import basiccoroutine.printWithThread
import kotlin.system.measureTimeMillis

fun example1() {
    runBlocking {
        basiccoroutine.printWithThread("START")
        launch {
            delay(2_000L) // yield()와 비슷
            basiccoroutine.printWithThread("LAUNCH END")
        }
        basiccoroutine.printWithThread("RUNBLOCKING END")
    }

    basiccoroutine.printWithThread("END")
}

fun example2(): Unit = runBlocking {
    val job = launch(start = CoroutineStart.LAZY) {
        basiccoroutine.printWithThread("Hello launch")
    }

    delay(1000)
    job.start()
}

fun example3(): Unit = runBlocking {
    val job = launch {
        (1..5).forEach {
            basiccoroutine.printWithThread(it)
            delay(500)
        }
    }

    delay(1000)
    job.cancel()
}

fun example4(): Unit = runBlocking {
    val job1 = launch {
        delay(1000)
        basiccoroutine.printWithThread("Job 1")
    }

    val job2 = launch {
        delay(1000)
        basiccoroutine.printWithThread("Job 2")
    }
}

fun example5(): Unit = runBlocking {
    val job = async {
        3 + 5
    }
    val result = job.await()
    basiccoroutine.printWithThread(result)
}

fun main(): Unit = runBlocking {
    val job1 = async { basiccoroutine.sec03.apiCall1() }
    val job2 = async { basiccoroutine.sec03.apiCall2() }

    kotlin.runCatching {
        basiccoroutine.printWithThread(job1.await() + job2.await())
    }.onFailure {
        if (it is RuntimeException) {
            println("예외발생 ${it.message}")
        }
    }
}

suspend fun apiCall1(): Int {
    delay(1000)
    throw RuntimeException()
//    return 1
}

suspend fun apiCall2(): Int {
    delay(1000)
    return 2
}
