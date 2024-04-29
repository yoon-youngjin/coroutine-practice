package standardcoroutine.test

import io.kotest.common.runBlocking
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class RepeatAddUseCaseTest {
    @Test
    fun `100번 더하면 100이 반환된다`() = runBlocking {
        // given
        val repeatAddUseCase = RepeatAddUseCase()

        // when
        val result = repeatAddUseCase.add(100)

        // then
        result shouldBe 100
    }
}
