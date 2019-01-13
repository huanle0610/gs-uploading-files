package hello

import org.mockito.Mockito

object MockitoHelper {
    fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    fun <T> uninitialized(): T = null as T
}
