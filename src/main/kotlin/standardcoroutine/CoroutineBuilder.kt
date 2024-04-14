package standardcoroutine

import kotlinx.coroutines.*

fun getElapsedTime(startTime: Long): String =
    "지난 시간: ${System.currentTimeMillis() - startTime}ms"

fun printJobState(job: Job) {
    println(
        "Job State\n" +
            "isActive >> ${job.isActive}\n" +
            "isCancelled >> ${job.isCancelled}\n" +
            "isCompleted >> ${job.isCompleted}"
    )
}

fun main() = runBlocking {
//    joinTest(this)
//    joinAllTest(this)
//    lazyJobTest(this)
//    cancelJobTest(this)
//    cancelAndJoinTest(this)
//    cancelCheckFailTest(this)
//    cancelCheckSuccessTest(this)
    jobStateTest(this)
}

suspend fun jobStateTest(coroutineScope: CoroutineScope) {
    val job = coroutineScope.launch {
        delay(1000)
    }
    job.cancelAndJoin()
    printJobState(job)
}

suspend fun cancelCheckSuccessTest(coroutineScope: CoroutineScope) {
    val whileJob = coroutineScope.launch(Dispatchers.Default) {
        while (this.isActive) {
                println("작업 중...")
        }
    }

    delay(100)
    whileJob.cancel()
}

suspend fun cancelCheckFailTest(coroutineScope: CoroutineScope) {
    val whileJob = coroutineScope.launch(Dispatchers.Default) {
        while (true) {
            println("작업 중...")
        }
    }
    delay(100)
    whileJob.cancel()
}

suspend fun cancelAndJoinTest(coroutineScope: CoroutineScope) {
    val startTime = System.currentTimeMillis()
    val longJob = coroutineScope.launch(Dispatchers.Default) {
        repeat(10) { repeatTime ->
            delay(1000)
            println("[${getElapsedTime(startTime)}] 반복횟수 $repeatTime")
        }
    }

    delay(3500)
    longJob.cancelAndJoin()
    executeAfterJobCancelled()
}

fun executeAfterJobCancelled() {
    val startTime = System.currentTimeMillis()
    println("[${Thread.currentThread().name}] [${getElapsedTime(startTime)}] 지연 실행")
}

suspend fun cancelJobTest(coroutineScope: CoroutineScope) {
    val startTime = System.currentTimeMillis()
    val longJob = coroutineScope.launch(Dispatchers.Default) {
        repeat(10) { repeatTime ->
            delay(1000)
            println("[${getElapsedTime(startTime)}] 반복횟수 $repeatTime")
        }
    }

    delay(3500)
    longJob.cancel()
}

suspend fun lazyJobTest(coroutineScope: CoroutineScope) {
    val startTime = System.currentTimeMillis()
    val lazyJob = coroutineScope.launch(start = CoroutineStart.LAZY) {
        println("[${Thread.currentThread().name}] [${getElapsedTime(startTime)}] 지연 실행")
    }

    delay(1000)
    lazyJob.start()
}

suspend fun joinAllTest(coroutineScope: CoroutineScope) {
    val convertImageJob1 = coroutineScope.launch(Dispatchers.Default) {
        delay(1000)
        println("[${Thread.currentThread().name}] 이미지1 변환 완료")
    }

    val convertImageJob2 = coroutineScope.launch(Dispatchers.Default) {
        delay(1000)
        println("[${Thread.currentThread().name}] 이미지2 변환 완료")
    }

    joinAll(convertImageJob1, convertImageJob2)

    coroutineScope.launch(Dispatchers.IO) {
        println("[${Thread.currentThread().name}] 이미지1, 이미지2 업로드")
    }
}

private suspend fun joinTest(coroutineScope: CoroutineScope) {
    val updateTokenJob = coroutineScope.launch(Dispatchers.IO) {
        println("[${Thread.currentThread().name}] 토큰 업데이트 시작")
        delay(100)
        println("[${Thread.currentThread().name}] 토큰 업데이트 완료")
    }
    updateTokenJob.join()

    coroutineScope.launch(Dispatchers.IO) {
        println("[${Thread.currentThread().name}] 네트워크 요청")
    }
}
