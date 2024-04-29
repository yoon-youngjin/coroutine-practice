package standardcoroutine.test

import kotlinx.coroutines.*

class StringStateHolder(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private val coroutineScope = CoroutineScope(coroutineDispatcher)

    var stringState = ""
        private set

    fun updateStringWithDelay(str: String) {
        coroutineScope.launch {
            delay(1000L)
            stringState = str
        }
    }
}