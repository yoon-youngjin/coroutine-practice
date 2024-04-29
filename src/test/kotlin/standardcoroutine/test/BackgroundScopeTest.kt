package standardcoroutine.test

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class BackgroundScopeTest {
    @Test
    fun `메인 스레드만 사용하는 runTest`() = runTest {
        println(Thread.currentThread())
    }

    @Test
    fun `끝나지 않아 실패하는 테스트`() = runTest {
        var result = 0

        launch {
            while (true) {
                delay(1000L)
                result += 1
            }
        }

        advanceTimeBy(1500L)
        result shouldBe 1
        advanceTimeBy(1000L)
        result shouldBe 2
    }

    @Test
    fun `backgrounScope를 사용하는 테스트`() = runTest {
        var result = 0

        backgroundScope.launch {
            while (true) {
                delay(1000L)
                result += 1
            }
        }

        advanceTimeBy(1500L)
        result shouldBe 1
        advanceTimeBy(1000L)
        result shouldBe 2
    }
}