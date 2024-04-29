package standardcoroutine.test

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class StringStateHolderTest {
    @Test
    fun `Fail - updateStringWithDelay("ABC")가 호출되면 문자열이 ABC로 변경된다`() = runTest {
        // given
        val stringStateHolder = StringStateHolder()

        // when
        stringStateHolder.updateStringWithDelay("ABC")

        // then
        advanceUntilIdle()
        stringStateHolder.stringState shouldNotBe "ABC"
    }

    @Test
    fun `Success - updateStringWithDelay("ABC")가 호출되면 문자열이 ABC로 변경된다`() {
        // given
        val testDispatcher = StandardTestDispatcher()
        val stringStateHolder = StringStateHolder(testDispatcher)

        // when
        stringStateHolder.updateStringWithDelay("ABC")

        // then
        testDispatcher.scheduler.advanceUntilIdle()
        stringStateHolder.stringState shouldBe "ABC"
    }
}