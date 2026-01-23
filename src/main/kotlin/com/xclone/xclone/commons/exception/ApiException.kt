package com.xclone.xclone.commons.exception

class ApiException(
    val code: ErrorCode,
    override val message: String = code.defaultMessage
) : RuntimeException(message)