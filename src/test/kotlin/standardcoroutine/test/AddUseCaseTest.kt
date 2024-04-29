package standardcoroutine.test

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AddUseCaseTest {
    lateinit var addUseCase: AddUseCase

    @BeforeEach
    fun setUp() {
        addUseCase = AddUseCase()
    }

    @Test
    fun `1 더하기 2는 3이다`() {
        // when
        val result = addUseCase.add(1, 2)

        // then
        result shouldBe 3
    }
}