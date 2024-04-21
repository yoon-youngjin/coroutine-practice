package standardcoroutine

import kotlinx.coroutines.*

fun main() = runBlocking {
//    exceptionPropagationTest(this)
//    blockingExceptionPropagationUsingJobTest(this)
//    limitBlockingExceptionPropagationUsingJobTest(this)
//    blockingExceptionPropagationUsingSupervisorJobTest1(this)
//    blockingExceptionPropagationUsingSupervisorJobTest2(this)
//    blockingExceptionPropagationUsingSupervisorJobWithCoroutineScopeTest()
//    mistakeSupervisorJobTest(this)
//    blockingExceptionPropagationUsingSupervisorScopeTest()
//    coroutineExceptionHandlerTest1()
//    coroutineExceptionHandlerTest2(this)
//    coroutineExceptionHandlerUsingJobTest(this)
//    coroutineExceptionHandlerUsingSupervisorJobTest(this)
//    mistakeCoroutineExceptionHandlerTest(this)
//    exceptionHandlingUsingTryCatchTest1(this)
//    exceptionHandlingUsingTryCatchTest2(this)
//    asyncExceptionHandlingTest()
//    mistakeAsyncExceptionHandlingTest(this)
//    blockingAsyncExceptionPropagationUsingSupervisorScopeTest()
//    cancellationExceptionTest1(this)
//    cancellationExceptionTest2(this)
//    withTimeOutTest(this)
    withTimeoutOrNullTest(this)
}

suspend fun withTimeoutOrNullTest(coroutineScope: CoroutineScope) {
    coroutineScope.launch(CoroutineName("Coroutine1")) {
        val result = withTimeoutOrNull(1000L) {
            delay(500L)
            return@withTimeoutOrNull "결과"
        }
        println(result)
    }
}

suspend fun withTimeOutTest(coroutineScope: CoroutineScope) {
    coroutineScope.launch(CoroutineName("Coroutine1")) {
        withTimeout(1000L) {
            delay(2000L)
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }
    }
    delay(2000)
    println("[${Thread.currentThread().name}] 코루틴 실행")
}

fun cancellationExceptionTest2(coroutineScope: CoroutineScope) {
    val job = coroutineScope.launch {
        delay(1000L)
    }
    job.invokeOnCompletion { exception ->
        println(exception)
    }
    job.cancel()
}

suspend fun cancellationExceptionTest(coroutineScope: CoroutineScope) {
    coroutineScope.launch(CoroutineName("Coroutine1")) {
        launch(CoroutineName("Coroutine2")) {
            throw CancellationException()
        }
        delay(100)
        println("[${Thread.currentThread().name}] 코루틴 실행")
    }
    delay(100)
    println("[${Thread.currentThread().name}] 코루틴 실행")
}

suspend fun blockingAsyncExceptionPropagationUsingSupervisorScopeTest() {
    supervisorScope {
        async(CoroutineName("Coroutine1")) {
            throw Exception("Coroutine1 예외 발생!!")
        }
        launch(CoroutineName("Coroutine2")) {
            delay(100)
            println("[${Thread.currentThread().name}] 코루틴 실행 완료")
        }
    }
}

suspend fun mistakeAsyncExceptionHandlingTest(coroutineScope: CoroutineScope) {
    val deferred = coroutineScope.async(CoroutineName("Coroutine1")) {
        throw Exception("Coroutine1 예외 발생!!")
    }
    try {
        deferred.await()
    } catch (e: Exception) {
        println("[노출된 예외] ${e.message}")
    }
    coroutineScope.launch(CoroutineName("Coroutine2")) {
        delay(100)
        println("[${Thread.currentThread().name}] 코루틴 실행 완료")
    }
}

suspend fun asyncExceptionHandlingTest() {
    supervisorScope {
        val deferred = async(CoroutineName("Coroutine1")) {
            throw Exception("Coroutine1 예외 발생!!")
        }
        try {
            deferred.await()
        } catch (e: Exception) {
            println("[노출된 예외] ${e.message}")
        }
    }
}

fun exceptionHandlingUsingTryCatchTest2(coroutineScope: CoroutineScope) {
    try {
        coroutineScope.launch(CoroutineName("Coroutine1")) {
            throw Exception("Coroutine1 예외 발생!!")
        }
    } catch (e: Exception) {
        println(e.message)
    }
    coroutineScope.launch(CoroutineName("Coroutine2")) {
        delay(1000)
        println("[${Thread.currentThread().name}] 코루틴 실행 완료")
    }
}

fun exceptionHandlingUsingTryCatchTest1(coroutineScope: CoroutineScope) {
    coroutineScope.launch(CoroutineName("Coroutine1")) {
        try {
            throw Exception("Coroutine1 예외 발생!!")
        } catch (e: Exception) {
            println(e.message)
        }
    }
    coroutineScope.launch(CoroutineName("Coroutine2")) {
        delay(1000)
        println("[${Thread.currentThread().name}] 코루틴 실행 완료")
    }
}

fun mistakeCoroutineExceptionHandlerTest(coroutineScope: CoroutineScope) {
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("[예외 발생] $throwable")
    }
    coroutineScope.launch(CoroutineName("Coroutine1") + exceptionHandler) {
        throw Exception("Coroutine1 예외 발생!!")
    }
}

suspend fun coroutineExceptionHandlerUsingSupervisorJobTest(coroutineScope: CoroutineScope) {
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("[예외 발생] $throwable")
    }
    val supervisedScope = CoroutineScope(SupervisorJob() + exceptionHandler)
    supervisedScope.apply {
        launch(CoroutineName("Coroutine1")) {
            throw Exception("Coroutine1 예외 발생!!")
        }
        launch(CoroutineName("Coroutine2")) {
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }
    }
    delay(1000)
}

suspend fun coroutineExceptionHandlerUsingJobTest(coroutineScope: CoroutineScope) {
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("[예외 발생] $throwable")
    }
    val coroutineContext = Job() + exceptionHandler
    coroutineScope.launch(CoroutineName("Coroutine1") + coroutineContext) {
        throw Exception("Coroutine1 예외 발생!!")
    }
    delay(1000)
}

suspend fun coroutineExceptionHandlerTest2(coroutineScope: CoroutineScope) {
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("[예외 발생] $throwable")
    }
    coroutineScope.launch(CoroutineName("Coroutine1") + exceptionHandler) {
        throw Exception("Coroutine1 예외 발생!!")
    }
    delay(1000)
}

suspend fun coroutineExceptionHandlerTest1() {
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("[예외 발생] $throwable")
    }
    CoroutineScope(exceptionHandler).launch(CoroutineName("Coroutine1")) {
        throw Exception("Coroutine1 예외 발생!!")
    }
    delay(1000)
}

suspend fun blockingExceptionPropagationUsingSupervisorScopeTest() {
    supervisorScope {
        launch(CoroutineName("Coroutine1")) {
            launch(CoroutineName("Coroutine3")) {
                throw Exception("예외 발생!")
            }
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }
        launch(CoroutineName("Coroutine2")) {
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }
    }
}

suspend fun mistakeSupervisorJobTest(coroutineScope: CoroutineScope) {
    coroutineScope.launch(CoroutineName("Parent Coroutine") + SupervisorJob()) {
        launch(CoroutineName("Coroutine1")) {
            launch(CoroutineName("Coroutine3")) {
                throw Exception("예외 발생!")
            }
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }
        launch(CoroutineName("Coroutine2")) {
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }
    }
    delay(1000L)
}

suspend fun blockingExceptionPropagationUsingSupervisorJobWithCoroutineScopeTest() {
    val coroutineScope = CoroutineScope(SupervisorJob())
    coroutineScope.launch(CoroutineName("Coroutine1")) {
        launch(CoroutineName("Coroutine3")) {
            throw Exception("예외 발생!")
        }
        delay(100L)
        println("[${Thread.currentThread().name}] 코루틴 실행")
    }

    coroutineScope.launch(CoroutineName("Coroutine2")) {
        delay(100L)
        println("[${Thread.currentThread().name}] 코루틴 실행")
    }
    delay(1000L)
}

suspend fun blockingExceptionPropagationUsingSupervisorJobTest2(coroutineScope: CoroutineScope) {
    val supervisorJob = SupervisorJob(coroutineScope.coroutineContext.job)
    coroutineScope.launch(CoroutineName("Coroutine1 Job") + supervisorJob) {
        launch(CoroutineName("Coroutine3")) {
            throw Exception("예외 발생!")
        }
        delay(100L)
        println("[${Thread.currentThread().name}] 코루틴 실행")
    }
    coroutineScope.launch(CoroutineName("Coroutine2") + supervisorJob) {
        delay(100L)
        println("[${Thread.currentThread().name}] 코루틴 실행")
    }
    supervisorJob.complete()
}

suspend fun blockingExceptionPropagationUsingSupervisorJobTest1(coroutineScope: CoroutineScope) {
    val supervisorJob = SupervisorJob()
    coroutineScope.launch(CoroutineName("Coroutine1 Job") + supervisorJob) {
        launch(CoroutineName("Coroutine3")) {
            throw Exception("예외 발생!")
        }
        delay(100L)
        println("[${Thread.currentThread().name}] 코루틴 실행")
    }
    coroutineScope.launch(CoroutineName("Coroutine2") + supervisorJob) {
        delay(100L)
        println("[${Thread.currentThread().name}] 코루틴 실행")
    }
    delay(1000L)
}

suspend fun limitBlockingExceptionPropagationUsingJobTest(coroutineScope: CoroutineScope) {
    val parentJob = coroutineScope.launch(CoroutineName("Parent Job")) {
        launch(CoroutineName("Coroutine1") + Job()) {// 새로운 Job 생성 -> 루트 코루틴
            launch(CoroutineName("Coroutine3")) {
                delay(100L)
                println("[${Thread.currentThread().name}] 코루틴 실행")
            }
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }
        launch(CoroutineName("Coroutine2")) {
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }
    }
    delay(20L)
    parentJob.cancel()
    delay(1000L)
}

suspend fun blockingExceptionPropagationUsingJobTest(coroutineScope: CoroutineScope) {
    coroutineScope.launch(CoroutineName("Parent Job")) {
        launch(CoroutineName("Coroutine1") + Job()) {// 새로운 Job 생성 -> 루트 코루틴
            launch(CoroutineName("Coroutine3")) {
                throw Exception("예외 발생!")
            }
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }
        launch(CoroutineName("Coroutine2")) {
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }
    }
    delay(1000L)
}

private suspend fun exceptionPropagationTest(coroutineScope: CoroutineScope) {
    coroutineScope.launch(CoroutineName("Coroutine1")) {
        launch(CoroutineName("Coroutine3")) {
            throw Exception("예외 발생!")
        }
        delay(100L)
        println("[${Thread.currentThread().name}] 코루틴 실행")
    }
    coroutineScope.launch(CoroutineName("Coroutine2")) {
        delay(100L)
        println("[${Thread.currentThread().name}] 코루틴 실행")
    }
    delay(1000L)
}