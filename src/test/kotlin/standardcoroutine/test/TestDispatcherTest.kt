package standardcoroutine.test

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.Instant

class TestDispatcherTest {
    @Test
    fun `StandardTestDispatcherTest`(): Unit = runBlocking {
        val testDispatcher = StandardTestDispatcher()

        // given
        var result = 0

        CoroutineScope(testDispatcher).launch {
            delay(10000L)
            result = 1
            delay(10000L)
            result = 2
            println(Thread.currentThread().name)
        }

        // then
        testDispatcher.scheduler.advanceUntilIdle()
        result shouldBe 2
    }

    @Test
    fun `UnconfinedTestDispatcherTest`(): Unit = runTest(UnconfinedTestDispatcher()) {
        // given
        var result = 0

        launch {
            println(Thread.currentThread().name)
            result = 2
        }

        // then
        result shouldBe 2
    }

    @Test
    fun test_withContext() = runTest {
        val currentTimeMs = Instant.now().toEpochMilli()
        withContext(Dispatchers.IO) {
            delay(500)
        }
        println("Done runTest : ${Instant.now().toEpochMilli() - currentTimeMs}")
    }
}