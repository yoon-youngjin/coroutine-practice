package standardcoroutine.test

import kotlinx.coroutines.delay

class RepeatAddWithDelayUseCase {
    suspend fun add(repeatTime: Int): Int {
        var result = 0
        repeat(repeatTime) {
            delay(100L)
            result += 1
        }
        return result
    }
}
