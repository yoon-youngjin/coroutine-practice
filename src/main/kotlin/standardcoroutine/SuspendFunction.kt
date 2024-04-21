package standardcoroutine

import kotlinx.coroutines.*

fun main() = runBlocking {
//    suspendfunTest()
//    callSuspendFunInCoroutine(this)
    callSuspendFunInAnotherSuspendFun()
}

suspend fun callSuspendFunInAnotherSuspendFun() {
    val startTime = System.currentTimeMillis()
    val results = searchByKeyword("Hello World")
    println("[결과] ${results.toList()}")
    println(getElapsedTime(startTime))
}

suspend fun searchByKeyword(keyword: String): Array<String> = supervisorScope {
    val dbResultsDeferred = async {
        throw Exception("searchFromDB 예외 발생!!")
        searchFromDB(keyword)
    }
    val serverResultsDeferred = async {
        searchFromServer(keyword)
    }

    val dbResults = try {
        dbResultsDeferred.await()
    } catch (e: Exception) {
        arrayOf()
    }

    val serverResults = try {
        serverResultsDeferred.await()
    } catch (e: Exception) {
        arrayOf()
    }

    return@supervisorScope arrayOf(*dbResults, *serverResults)
}

suspend fun searchFromDB(keyword: String): Array<String> {
    delay(1000L)
    return arrayOf("[DB]${keyword}1")
}

suspend fun searchFromServer(keyword: String): Array<String> {
    delay(1000L)
    return arrayOf("[Server]${keyword}1")
}

suspend fun callSuspendFunInCoroutine(coroutineScope: CoroutineScope) {
    delayAndPrint(keyword = "I'm Parent Coroutine")
    coroutineScope.launch {
        delayAndPrint(keyword = "I'm Child Coroutine")
    }
}

suspend fun delayAndPrint(keyword: String) {
    delay(1000L)
    println(keyword)
}

private fun CoroutineScope.suspendfunTest() {
    val startTime = System.currentTimeMillis()
    launch {
        delayWithPrintHelloWorld()
    }
    launch {
        delayWithPrintHelloWorld()
    }
    println(getElapsedTime(startTime))
}

suspend fun delayWithPrintHelloWorld() {
    delay(1000L)
    println("Hello World")
}
