package sec04

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import printWithThread

fun main(): Unit = runBlocking {
    val job = launch(Dispatchers.Default) {
        var i = 1
        var nextPrintTime = System.currentTimeMillis()
        while (i <= 5) {
            if (nextPrintTime <= System.currentTimeMillis()) {
                printWithThread("${i++}번째 출력! $nextPrintTime")
                nextPrintTime += 1000
            }

            if (!isActive) {
                throw CancellationException()
            }
        }
    }

    delay(100)
    job.cancel()
}

fun example(): Unit = runBlocking {
    val job1 = launch {
        delay(1000)
        printWithThread("Job 1")
    }

    val job2 = launch {
        delay(1000)
        printWithThread("Job 2")
    }

    delay(100)
    job1.cancel()
}

