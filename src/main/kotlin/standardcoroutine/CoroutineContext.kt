@file:OptIn(ExperimentalStdlibApi::class)

package standardcoroutine

import kotlinx.coroutines.*
import kotlin.coroutines.EmptyCoroutineContext

val emptyCoroutineContext = EmptyCoroutineContext
fun main() = runBlocking<Unit> {
//    coroutineContextTest(this)
//    overwriteCoroutineContextTest1(this)
//    overwriteCoroutineContextTest2(this)
//    accessCoroutineContextByKey()
//    accessCoroutineContextByKeyProperty()
    minusKeyTest()
}

fun minusKeyTest() {
    val coroutineName = CoroutineName("myCoroutine")
    val dispatcher = Dispatchers.IO
    val coroutineContext = coroutineName + dispatcher

    val deletedCoroutineContext = coroutineContext.minusKey(CoroutineName)
    println(deletedCoroutineContext[coroutineName.key])
    println(deletedCoroutineContext[dispatcher.key])
    println(deletedCoroutineContext[Job])
}

fun accessCoroutineContextByKeyProperty() {
    val coroutineName = CoroutineName("myCoroutine")
    val dispatcher = Dispatchers.IO
    val coroutineContext = coroutineName + dispatcher

    println(coroutineContext[coroutineName.key])
    println(coroutineContext[dispatcher.key])
}


fun accessCoroutineContextByKey() {
    val coroutineContext = CoroutineName("myCoroutine") + Dispatchers.IO
//    val nameFromContext = coroutineContext[CoroutineName.Key]
    val nameFromContext = coroutineContext[CoroutineName]
    println(nameFromContext)
}

fun overwriteCoroutineContextTest1(coroutineScope: CoroutineScope) {
    val coroutineContext = newSingleThreadContext("myThread") + CoroutineName("myCoroutine")

    val newCoroutineContext = coroutineContext + CoroutineName("newCoroutine")
    coroutineScope.launch(newCoroutineContext) {
        println("[${Thread.currentThread().name}] 실행")
    }
}

fun overwriteCoroutineContextTest2(coroutineScope: CoroutineScope) {
    val coroutineContext1 = newSingleThreadContext("myThread1") + CoroutineName("myCoroutine1")
    val coroutineContext2 = newSingleThreadContext("myThread2") + CoroutineName("myCoroutine2")

    val combinedCoroutineContext = coroutineContext1 + coroutineContext2
    coroutineScope.launch(combinedCoroutineContext) {
        println("[${Thread.currentThread().name}] 실행")
    }
}

fun coroutineContextTest(coroutineScope: CoroutineScope) {
    val coroutineContext = newSingleThreadContext("myThread") + CoroutineName("myCoroutine")

    coroutineScope.launch(coroutineContext) {
        println("[${Thread.currentThread().name}] 실행")
    }
}
