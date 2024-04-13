package basiccoroutine.sec08

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import basiccoroutine.printWithThread

fun main(): Unit = runBlocking {
    repeat(10) {
        launch {
            basiccoroutine.sec08.a()
            basiccoroutine.sec08.b()
        }

        launch {
            basiccoroutine.sec08.c()
        }
    }
}

suspend fun a() {
    basiccoroutine.printWithThread("A")
}

suspend fun b() {
    basiccoroutine.printWithThread("B")
}

suspend fun c() {
    basiccoroutine.printWithThread("C")
}

