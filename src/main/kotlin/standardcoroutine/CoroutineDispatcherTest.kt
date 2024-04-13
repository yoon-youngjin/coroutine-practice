package standardcoroutine

import kotlinx.coroutines.*

val singleThreadDispatcher = newSingleThreadContext("SingleThread")
val multiThreadDispatcher = newFixedThreadPoolContext(
    nThreads = 2,
    name = "MultiThread"
)

//fun main() = runBlocking<Unit> {
//    println("[${Thread.currentThread().name}] 실행")
//    launch(multiThreadDispatcher) {
//        println("[${Thread.currentThread().name}] 실행")
//        launch {
//            println("[${Thread.currentThread().name}] 실행")
//        }
//        launch {
//            println("[${Thread.currentThread().name}] 실행")
//        }
//    }
//}

//fun main() = runBlocking<Unit> {
//    launch(Dispatchers.IO) {
//        repeat(30) {
//            launch {
//                Thread.sleep(100)
//                println("[${Thread.currentThread().name}] 코루틴 실행")
//            }
//        }
//    }
//}

fun main() = runBlocking<Unit> {
    launch(Dispatchers.IO.limitedParallelism(500)) {
        repeat(500) {
            launch {
                Thread.sleep(1000)
                println("[${Thread.currentThread().name}] 코루틴 실행")
            }
        }
    }
}