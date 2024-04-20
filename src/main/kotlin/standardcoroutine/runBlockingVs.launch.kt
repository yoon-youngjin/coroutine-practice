package standardcoroutine

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

//fun main() = runBlocking {
//    launch(CoroutineName("Coroutine1")) {
//        delay(1000)
//        println("[${Thread.currentThread().name}] launch 코루틴 종료")
//    }
//    delay(2000)
//    println("[${Thread.currentThread().name}] runBlocking 코루틴 종료")
//}

//fun main() = runBlocking {
//    val startTime = System.currentTimeMillis()
//    runBlocking(CoroutineName("Coroutine1")) {
//        delay(1000)
//        println("[${Thread.currentThread().name}] 하위 코루틴 종료")
//    }
//
//    println(getElapsedTime(startTime))
//}

fun main() = runBlocking {
    val startTime = System.currentTimeMillis()
    launch(CoroutineName("Coroutine1")) {
        delay(1000)
        println("[${Thread.currentThread().name}] 하위 코루틴 종료")
    }

    println(getElapsedTime(startTime))
}

