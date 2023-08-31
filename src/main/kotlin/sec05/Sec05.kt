package sec05

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import printWithThread

fun example1(): Unit = runBlocking {
    val job1 = CoroutineScope(Dispatchers.Default).launch {
        delay(1000)
        printWithThread("Job 1")
    }

    val job2 = CoroutineScope(Dispatchers.Default).launch {
        delay(1000)
        printWithThread("Job 2")
    }
}

fun example2(): Unit = runBlocking {
    val job = launch {
        try {
            throw IllegalArgumentException()
        } catch (e: IllegalArgumentException) {
            printWithThread("정상 종료")
        }
    }
}

fun main(): Unit = runBlocking {
    val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        printWithThread("예외")
    }
    val job = CoroutineScope(Dispatchers.Default).launch(exceptionHandler) {
        throw IllegalArgumentException()
    }

    delay(1000)
}


