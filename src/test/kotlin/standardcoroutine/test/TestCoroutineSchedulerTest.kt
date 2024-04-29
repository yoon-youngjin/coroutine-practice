package standardcoroutine.test

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.Test

class TestCoroutineSchedulerTest {
    @Test
    fun `가상 시간 조절 테스트`() {
        val testCoroutineScheduler = TestCoroutineScheduler()

        testCoroutineScheduler.advanceTimeBy(5000L)
        testCoroutineScheduler.currentTime shouldBe 5000L

        testCoroutineScheduler.advanceTimeBy(6000L)
        testCoroutineScheduler.currentTime shouldBe 11000L

        testCoroutineScheduler.advanceTimeBy(10000L)
        testCoroutineScheduler.currentTime shouldBe 21000L
    }

    @Test
    fun `가상 시간 위에서 테스트 진행`() {
        val testCoroutineScheduler = TestCoroutineScheduler()
        val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
        val testCoroutineScope = CoroutineScope(testDispatcher)

        // given
        var result = 0

        // when
        testCoroutineScope.launch {
            delay(10000L)
            result = 1
            delay(10000L)
            result = 2
            println(Thread.currentThread().name)
        }

        // then
        result shouldBe 0
        testCoroutineScheduler.advanceTimeBy(5000L)
        result shouldBe 0
        testCoroutineScheduler.advanceTimeBy(6000L)
        result shouldBe 1
        testCoroutineScheduler.advanceTimeBy(10000L)
        result shouldBe 2
    }

    @Test
    fun `advanceUntilIdle의 동작 살펴보기`() {
        val testCoroutineScheduler = TestCoroutineScheduler()
        val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
        val testCoroutineScope = CoroutineScope(testDispatcher)

        // given
        var result = 0

        // when
        testCoroutineScope.launch {
            delay(10000L)
            result = 1
            delay(10000L)
            result = 2
            println(Thread.currentThread().name)
        }
        testCoroutineScheduler.advanceUntilIdle()

        // then
        result shouldBe 2
    }

    @Test
    fun `StandardTestDispatcher 사용하기`() {
        val testDispatcher = StandardTestDispatcher()
        val testCoroutineScope = CoroutineScope(testDispatcher)

        // given
        var result = 0

        // when
        testCoroutineScope.launch {
            delay(10000L)
            result = 1
            delay(10000L)
            result = 2
            println(Thread.currentThread().name)
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        result shouldBe 2
    }

    @Test
    fun `TestScope 사용하기`() {
        val testCoroutineScope = TestScope()

        // given
        var result = 0

        // when
        testCoroutineScope.launch {
            delay(10000L)
            result = 1
            delay(10000L)
            result = 2
            println(Thread.currentThread().name)
        }
        testCoroutineScope.advanceUntilIdle()

        // then
        result shouldBe 2
    }

    @Test
    fun `runTest 사용하기1`() {
        // given
        var result = 0

        // when
        runTest {
            delay(10000L)
            result = 1
            delay(10000L)
            result = 2
            println(Thread.currentThread().name)
        }

        // then
        result shouldBe 2
    }

    @Test
    fun `runTest 사용하기2`() = runTest {
        // given
        var result = 0

        // when
        delay(10000L)
        result = 1
        delay(10000L)
        result = 2
        println(Thread.currentThread().name)

        // then
        result shouldBe 2
    }

    @Test
    fun `runTest에서 가상 시간 확인`() = runTest {
        // when
        delay(10000L)
        println("가상 시간: ${this.currentTime}")
        delay(10000L)
        println("가상 시간: ${this.currentTime}")
    }

    @Test
    fun `runTest 내부에서 advanceUntilIdle 사용하기`() = runTest {
        var result = 0
        launch {
            delay(1000L)
            result = 1
        }

        println("가상 시간: ${this.currentTime}ms, result = $result") // 0
        advanceUntilIdle()
        println("가상 시간: ${this.currentTime}ms, result = $result") // 1
    }
}