package standardcoroutine

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

fun main() = runBlocking {
//    inheritanceTest1(this)
//    inheritanceTest2(this)
//    inheritanceTest3(this)
//    parentChildTest(this)
//    spreadTest(this)
//    childDependentTest(this)
//    invokeOnCompletionCancelTest(this)
//    executionCompletedStatusTest(this)
//    coroutineScopeFnTest1()
//    coroutineScopeFnTest2()
//    coroutineScopeTest()
//    escapeCoroutineScopeTest(this)
//    coroutineScopeCancelTest(this)
//    coroutineScopeIsActiveTest(this)
//    breakStructuredConcurrencyUsingCoroutineScope()
//    breakStructuredConcurrencyUsingJob1(this)
//    breakStructuredConcurrencyUsingJob2(this)
//    stipulateJobParentTest(this)
    warningJobConstructorTest(this)
}

fun warningJobConstructorTest(coroutineScope: CoroutineScope) {
    coroutineScope.launch(CoroutineName("Coroutine1")){
        val coroutineJob = this.coroutineContext[Job]
        val newJob = Job(coroutineJob)
        launch(CoroutineName("Coroutine2") + newJob) {
            delay(1000)
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }.invokeOnCompletion {
            newJob.complete()
        }
    }
}

fun stipulateJobParentTest(coroutineScope: CoroutineScope) {
    coroutineScope.launch(CoroutineName("Coroutine1")){
        val coroutineJob = this.coroutineContext[Job]
        val newJob = Job(coroutineJob)
        launch(CoroutineName("Coroutine2") + newJob) {
            delay(1000)
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }
    }
}


suspend fun breakStructuredConcurrencyUsingJob2(coroutineScope: CoroutineScope) {
    val newRootJob = Job()
    coroutineScope.launch(CoroutineName("Coroutine1") + newRootJob) {
        launch(CoroutineName("Coroutine3")) {
            delay(1000)
            println("[${Thread.currentThread().name}] 코루틴 실행 완료")
        }
        launch(CoroutineName("Coroutine4")) {
            delay(1000)
            println("[${Thread.currentThread().name}] 코루틴 실행 완료")
        }
    }
    coroutineScope.launch(CoroutineName("Coroutine2") + newRootJob) {
        launch(CoroutineName("Coroutine5") + Job()) {
            delay(1000)
            println("[${Thread.currentThread().name}] 코루틴 실행 완료")
        }
    }
    delay(50) // 모든 코루틴 생성 대기 시간
    newRootJob.cancel()
    delay(2000)
}

suspend fun breakStructuredConcurrencyUsingJob1(coroutineScope: CoroutineScope) {
    val newRootJob = Job()
    coroutineScope.launch(CoroutineName("Coroutine1") + newRootJob) {
        launch(CoroutineName("Coroutine3")) {
            delay(1000)
            println("[${Thread.currentThread().name}] 코루틴 실행 완료")
        }
        launch(CoroutineName("Coroutine4")) {
            delay(1000)
            println("[${Thread.currentThread().name}] 코루틴 실행 완료")
        }
    }
    coroutineScope.launch(CoroutineName("Coroutine2") + newRootJob) {
        delay(1000)
        println("[${Thread.currentThread().name}] 코루틴 실행 완료")
    }
    newRootJob.cancel()
    delay(2000)
}

fun breakStructuredConcurrencyUsingCoroutineScope() {
    val newScope = CoroutineScope(Dispatchers.IO)
    newScope.launch {
        launch {
            delay(1000)
            println("[${Thread.currentThread().name}] 코루틴 실행 완료")
        }
        launch {
            delay(1000)
            println("[${Thread.currentThread().name}] 코루틴 실행 완료")
        }
    }
    newScope.launch {
        delay(1000)
        println("[${Thread.currentThread().name}] 코루틴 실행 완료")
    }
}

suspend fun coroutineScopeIsActiveTest(coroutineScope: CoroutineScope) {
    val whileJob = coroutineScope.launch(Dispatchers.Default) {
        while (this.isActive) {
            println("작업 중")
        }
    }
    delay(100L)
    whileJob.cancel()
}

fun coroutineScopeCancelTest(coroutineScope: CoroutineScope) {
    coroutineScope.launch(CoroutineName("Coroutine1")) {
        launch(CoroutineName("Coroutine3")) {
            delay(1000)
            println("[${Thread.currentThread().name}] 코루틴 실행 완료")
        }
        launch(CoroutineName("Coroutine4")) {
            delay(1000)
            println("[${Thread.currentThread().name}] 코루틴 실행 완료")
        }
        this.cancel()
    }

    coroutineScope.launch(CoroutineName("Coroutine2")) {
        delay(1000)
        println("[${Thread.currentThread().name}] 코루틴 실행 완료")
    }
}

fun escapeCoroutineScopeTest(coroutineScope: CoroutineScope) {
    coroutineScope.launch(CoroutineName("Coroutine1")) {
        launch(CoroutineName("Coroutine3")) {
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }
        val newCoroutineScope = CoroutineScope(Dispatchers.IO)
        newCoroutineScope.launch(CoroutineName("Coroutine4")) {
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }
        newCoroutineScope.launch(CoroutineName("Coroutine5")) {
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }
    }

    coroutineScope.launch(CoroutineName("Coroutine2")) {
        println("[${Thread.currentThread().name}] 코루틴 실행")
    }
}

@OptIn(ExperimentalStdlibApi::class)
fun coroutineScopeTest() {
    val newScope = CoroutineScope(CoroutineName("MyCoroutine") + Dispatchers.IO)
    newScope.launch(CoroutineName("LaunchCoroutine")) {
        println(this.coroutineContext[CoroutineName])
        println(this.coroutineContext[CoroutineDispatcher])
        val launchJob = this.coroutineContext[Job]
        val newScopeJob = newScope.coroutineContext[Job]
        println("launchJob?.parent === newScopeJob >> ${launchJob?.parent === newScopeJob}")
    }
    Thread.sleep(1000)
}

fun coroutineScopeFnTest2() {
//    val coroutineContext = Job() + newSingleThreadContext("CustomScopeThread")
    val customCoroutineScope = CoroutineScope(Dispatchers.IO)
    customCoroutineScope.launch {
        delay(1000)
        println("[${Thread.currentThread().name}] 코루틴 실행 완료")
    }
    Thread.sleep(2000) // 코드 종료 방지
}

class CustomCoroutineScope : CoroutineScope {
    override val coroutineContext: CoroutineContext = Job() + newSingleThreadContext("CustomScopeThread")
}

fun coroutineScopeFnTest1() {
    val coroutineScope = CustomCoroutineScope()
    coroutineScope.launch {
        delay(1000)
        println("[${Thread.currentThread().name}] 코루틴 실행 완료")
    }
    Thread.sleep(2000) // 코드 종료 방지
}

suspend fun executionCompletedStatusTest(coroutineScope: CoroutineScope) {
    val startTime = System.currentTimeMillis()
    val parentJob = coroutineScope.launch {
        launch {
            delay(1000)
            println("[${getElapsedTime(startTime)}] 자식 코루틴 실행 완료")
        }
        println("[${getElapsedTime(startTime)}] 부모 코루틴 실행하는 마지막 코드")
    }
    parentJob.invokeOnCompletion {// 부모 코루틴이 종료될 시 호출되는 콜백 등록
        println("[${getElapsedTime(startTime)}] 부모 코루틴 실행 완료")
    }

    delay(500)
    printJobState(parentJob)
}

fun invokeOnCompletionCancelTest(coroutineScope: CoroutineScope) {
    val infiniteJob = coroutineScope.launch {
        while (true) {
            delay(1000)
        }
    }
    infiniteJob.invokeOnCompletion {
        println("invokeOnCompletion 콜백 실행")
    }
    infiniteJob.cancel()
}

fun childDependentTest(coroutineScope: CoroutineScope) {
    val startTime = System.currentTimeMillis()
    val parentJob = coroutineScope.launch {
        launch {
            delay(1000)
            println("[${getElapsedTime(startTime)}] 자식 코루틴 실행 완료")
        }
        println("[${getElapsedTime(startTime)}] 부모 코루틴 실행하는 마지막 코드")
    }
//        .invokeOnCompletion {
//        println("[${getElapsedTime(startTime)}] 부모 코루틴 실행 완료")
//    }
    parentJob.invokeOnCompletion {// 부모 코루틴이 종료될 시 호출되는 콜백 등록
        println("[${getElapsedTime(startTime)}] 부모 코루틴 실행 완료")
    }
}

fun spreadTest(coroutineScope: CoroutineScope) {
    val parentJob = coroutineScope.launch(Dispatchers.IO) {
        val dbResultsDeferred: List<Deferred<String>> = listOf("db1", "db2", "db3").map {
            async {
                delay(1000)
                println("${it}으로부터 데이터를 가져오는데 성공했습니다.")
                return@async "[$it]data"
            }
        }
        val dbResults = dbResultsDeferred.awaitAll() // 모든 코루틴이 완료될 때까지 대기
        println(dbResults)
    }
    parentJob.cancel()
}

fun parentChildTest(coroutineScope: CoroutineScope) {
    val parentJob = coroutineScope.coroutineContext[Job]
    coroutineScope.launch {
        val childJob = coroutineContext[Job]

        println("1. 부모 코루틴과 자식 코루틴의 Job은 같은가? ${parentJob === childJob}")
        println("2. 자식 코루틴의 Job이 가지고 있는 parent는 부모 코루틴의 Job인가? ${childJob?.parent === parentJob}")
        println(
            "3. 부모 코루틴의 Job은 자식 코루틴의 Job에 대한 참조를 가지는가? ${
                parentJob?.let {
                    it.children.contains(childJob)
                }
            }"
        )
    }
}

/**
 * Job 객체는 상속되지 않는다.
 */
fun inheritanceTest3(coroutineScope: CoroutineScope) {
    val runBlockingJob = coroutineScope.coroutineContext[Job] // 부모 코루틴의 coroutineContext로부터 Job 추출

    coroutineScope.launch {
        val launchJob = coroutineContext[Job] // 자식 코루틴의 coroutineContext로부터 Job 추출
        if (runBlockingJob === launchJob) {
            println("runBlocking으로 생성된 Job과 launch로 생성된 Job이 동일하다.")
        } else {
            println("runBlocking으로 생성된 Job과 launch로 생성된 Job이 다르다.")
        }
    }
}

fun inheritanceTest1(coroutineScope: CoroutineScope) {
    val coroutineContext = newSingleThreadContext("MyThread") + CoroutineName("CoroutineA")

    coroutineScope.launch(coroutineContext) {
        println("[${Thread.currentThread().name}] 부모 코루틴 실행")
        launch {
            println("[${Thread.currentThread().name}] 자식 코루틴 실행")
        }
    }
}

fun inheritanceTest2(coroutineScope: CoroutineScope) {
    val coroutineContext1 = newSingleThreadContext("MyThread") + CoroutineName("ParentCoroutine")
    val coroutineContext2 = CoroutineName("ChildCoroutine")

    coroutineScope.launch(coroutineContext1) {
        println("[${Thread.currentThread().name}] 부모 코루틴 실행")
        launch(coroutineContext2) {
            println("[${Thread.currentThread().name}] 자식 코루틴 실행")
        }
    }
}
