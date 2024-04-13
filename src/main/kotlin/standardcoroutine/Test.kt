package standardcoroutine

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking(context = CoroutineName("Parent")) {
    println("[${Thread.currentThread().name}] 실행")
    test(this)
}

fun test(coroutineScope: CoroutineScope) {
    coroutineScope.launch(context = CoroutineName("Child1")) {
        println("[${Thread.currentThread().name}] 실행")
    }
    coroutineScope.launch(context = CoroutineName("Child2")) {
        println("[${Thread.currentThread().name}] 실행")
    }
}