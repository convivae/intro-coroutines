package samples

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SampleTest {
    @Test
    fun testDelayInSuspend() = runBlockingTest {
        val realStartTime = System.currentTimeMillis()
        val virtualStartTime = System.currentTimeMillis()

        foo()

        println("${System.currentTimeMillis() - realStartTime} ms")  // ~ 6 ms
        println("${System.currentTimeMillis() - virtualStartTime} ms")              // 1000 ms
    }

    suspend fun foo() {
        delay(1000) // auto-advances without delay
        println("foo")       // executes eagerly when foo() is called
    }

    @Test
    fun testDelayInLaunch() = runBlockingTest {
        val realStartTime = System.currentTimeMillis()
        val virtualStartTime = System.currentTimeMillis()

        bar()

        println("${System.currentTimeMillis() - realStartTime} ms")  // ~ 11 ms
        println("${System.currentTimeMillis() - virtualStartTime} ms")              // 1000 ms
    }

    suspend fun bar() = coroutineScope {
        launch {
            delay(1000) // auto-advances without delay
            println("bar")       // executes eagerly when bar() is called
        }
    }
}