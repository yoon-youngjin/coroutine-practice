package sec03

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import printWithThread
import kotlin.system.measureTimeMillis

fun example1() {
    runBlocking {
        printWithThread("START")
        launch {
            delay(2_000L) // yield()와 비슷
            printWithThread("LAUNCH END")
        }
    }

    printWithThread("END")
}

fun example2(): Unit = runBlocking {
    val job = launch(start = CoroutineStart.LAZY) {
        printWithThread("Hello launch")
    }

    delay(1000)
    job.start()
}

fun example3(): Unit = runBlocking {
    val job = launch {
        (1..5).forEach {
            printWithThread(it)
            delay(500)
        }
    }

    delay(1000)
    job.cancel()
}

fun example4(): Unit = runBlocking {
    val job1 = launch {
        delay(1000)
        printWithThread("Job 1")
    }

    val job2 = launch {
        delay(1000)
        printWithThread("Job 2")
    }
}

fun example5(): Unit = runBlocking {
    val job = async {
        3 + 5
    }
    val result = job.await()
    printWithThread(result)
}

fun main(): Unit = runBlocking {
    val time = measureTimeMillis {
        val job1 = async(start = CoroutineStart.LAZY) { apiCall1() }
        val job2 = async(start = CoroutineStart.LAZY) { apiCall2() }

        job1.start()
        job2.start()
        printWithThread(job1.await() + job2.await())

    }

    printWithThread("소요 시간 $time ms")
}

suspend fun apiCall1(): Int {
    delay(1000)
    return 1
}

suspend fun apiCall2(): Int {
    delay(1000)
    return 2
}
