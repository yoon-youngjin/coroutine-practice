package sec08

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import printWithThread

fun main(): Unit = runBlocking {
    repeat(10) {
        launch {
            a()
            b()
        }

        launch {
            c()
        }
    }
}

suspend fun a() {
    printWithThread("A")
}

suspend fun b() {
    printWithThread("B")
}

suspend fun c() {
    printWithThread("C")
}

