package com.example.fructus.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ClickGuard {
    var isLocked = false
        private set

    fun tryLock(scope: CoroutineScope, lockDuration: Long = 300L, onClick: () -> Unit) {
        if (isLocked) return
        isLocked = true
        onClick()
        scope.launch {
            delay(lockDuration)
            isLocked = false
        }
    }
}
