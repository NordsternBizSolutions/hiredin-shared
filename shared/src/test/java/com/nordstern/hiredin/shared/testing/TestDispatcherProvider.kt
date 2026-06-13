package com.nordstern.hiredin.shared.testing

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatcherProvider(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) {
    val main: CoroutineDispatcher get() = testDispatcher
    val io: CoroutineDispatcher get() = testDispatcher
    val default: CoroutineDispatcher get() = testDispatcher
}
