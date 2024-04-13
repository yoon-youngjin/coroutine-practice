package basiccoroutine.sec04

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import basiccoroutine.printWithThread

fun example2(): Unit = runBlocking {
    val job = launch(Dispatchers.Default) {
        var i = 1
        var nextPrintTime = System.currentTimeMillis()
        while (i <= 5) {
            if (nextPrintTime <= System.currentTimeMillis()) {
                basiccoroutine.printWithThread("${i++}번째 출력! $nextPrintTime")
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
        basiccoroutine.printWithThread("Job 1")
    }

    val job2 = launch {
        delay(1000)
        basiccoroutine.printWithThread("Job 2")
    }

    delay(100)
    job1.cancel()
}

fun main() = runBlocking {
    val job = launch {
        try {
        } catch (e: CancellationException) {
        }
        basiccoroutine.printWithThread("try-catch에 의해 취소되지 않았다.(원래는 해당 라인이 실행되면 X)")
    }

    delay(100)
    basiccoroutine.printWithThread("취소 시작")
    job.cancel()
}
