package basiccoroutine.sec07

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import basiccoroutine.printWithThread

fun example() {
    CoroutineScope(Dispatchers.Default).launch {
        basiccoroutine.printWithThread("Job 1")
    }

    Thread.sleep(500)
}

suspend fun example3() {
    val job = CoroutineScope(Dispatchers.Default).launch {
        basiccoroutine.printWithThread("Job 1")
    }

    job.join()
}

suspend fun example4() {
    val job = CoroutineScope(Dispatchers.Default).launch {
        basiccoroutine.printWithThread("Job 1")
        coroutineContext + CoroutineName("테스트") + Dispatchers.Main
        coroutineContext.minusKey(CoroutineName.Key)
        basiccoroutine.printWithThread(coroutineContext.isActive)
        basiccoroutine.printWithThread(coroutineContext.job)
    }
    job.join()
}

suspend fun main() {
    basiccoroutine.sec07.example4()
//    val threadPool = Executors.newSingleThreadExecutor()
//    CoroutineScope(threadPool.asCoroutineDispatcher()).launch {
//
//    }
}


class AsyncLogic {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun doSomeThing() {
        scope.launch {
            // 작업
        }
    }

    fun destroy() {
        scope.cancel()
    }
}