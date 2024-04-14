package standardcoroutine

import kotlinx.coroutines.*

fun main() = runBlocking {
//    deferredTest(this)
//    multiAsyncTest(this)
//    withContextTest1(this)
//    withContextTest2(this)
//    withContextTest3(this)
    withContextTest4()

}

suspend fun withContextTest4() {
    val startTime = System.currentTimeMillis()
    val helloDeferred = withContext(Dispatchers.IO) {
        println("[${Thread.currentThread().name}] helloDeferred 블록 실행")
        delay(1000)
        return@withContext "hello"
    }
    val worldDeferred = withContext(Dispatchers.IO) {
        println("[${Thread.currentThread().name}] worldDeferred 블록 실행")
        delay(1000)
        return@withContext "world"
    }

    println("[${getElapsedTime(startTime)}] ${helloDeferred}, $worldDeferred")
}

suspend fun withContextTest3(coroutineScope: CoroutineScope) {
    println("[${Thread.currentThread().name}] runBlocking 블록 실행")
    coroutineScope.async(Dispatchers.IO) {
        println("[${Thread.currentThread().name}] withContext 블록 실행")
    }.await()
}
suspend fun withContextTest2(coroutineScope: CoroutineScope) {
    delay(10000)
    println("[${Thread.currentThread().name}] runBlocking 블록 실행")
    withContext(Dispatchers.IO) {
        println("[${Thread.currentThread().name}] withContext 블록 실행")
    }
}
suspend fun withContextTest1(coroutineScope: CoroutineScope) {
    val result = withContext(Dispatchers.IO) {
        delay(1000)
        return@withContext "Dummy Response"
    }
    println(result)
}

suspend fun multiAsyncTest(coroutineScope: CoroutineScope) {
    val startTime = System.currentTimeMillis()
    val participantDeferred1 = coroutineScope.async(Dispatchers.IO) {
        delay(1000)
        return@async arrayOf("James", "Jason")
    }
    val participantDeferred2 = coroutineScope.async(Dispatchers.IO) {
        delay(1000)
        return@async arrayOf("Jenny")
    }

//    val results = awaitAll(participantDeferred1, participantDeferred2)
    val results = listOf(participantDeferred1, participantDeferred2).awaitAll()

    println("[${getElapsedTime(startTime)}] 참여자 목록: ${listOf(*results[0], *results[1])}")
}

private suspend fun deferredTest(coroutineScope: CoroutineScope) {
    val networkDeferred: Deferred<String> = coroutineScope.async(Dispatchers.IO) {
        delay(1000L)
        return@async "Dummy Response"
    }

    val result = networkDeferred.await()
    println(result)
}