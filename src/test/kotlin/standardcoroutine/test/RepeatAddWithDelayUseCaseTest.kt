package standardcoroutine.test

import io.kotest.common.runBlocking
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import org.junit.jupiter.api.Test

class RepeatAddWithDelayUseCaseTest {
    private val testCoroutineScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
    @Test
    fun `100번 더하면 100이 반환된다`() = runBlocking {
        // given
        val repeatAddWithDelayUseCase = RepeatAddWithDelayUseCase()

        // when
        var result = 0
        CoroutineScope(testDispatcher).launch {
            result = repeatAddWithDelayUseCase.add(100)
        }

        // then
        testCoroutineScheduler.advanceUntilIdle()
        result shouldBe 100
    }
}
