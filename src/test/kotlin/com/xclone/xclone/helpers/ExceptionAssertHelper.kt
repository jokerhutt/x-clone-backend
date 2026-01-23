package com.xclone.xclone.helpers

import org.junit.jupiter.api.Assertions.assertThrows

object ExceptionAssertHelper {

    fun assertIllegalState(executable: () -> Unit) {
        assertThrows(IllegalStateException::class.java) {
            executable()
        }
    }
}