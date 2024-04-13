package basiccoroutine.sec02

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import basiccoroutine.printWithThread

fun main(): Unit = runBlocking {
    basiccoroutine.printWithThread("START")
    launch {
        basiccoroutine.sec02.newRoutine()
    }
    yield()
    basiccoroutine.printWithThread("END")
}

// 코루틴에 사용되는 루틴(함수)를 표현하기 위해 suspend 키워드
suspend fun newRoutine() {
    val num1 = 1
    val num2 = 2
    yield()
    basiccoroutine.printWithThread("${num1 + num2}")
}
