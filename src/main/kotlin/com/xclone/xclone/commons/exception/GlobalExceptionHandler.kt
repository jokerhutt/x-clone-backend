package com.xclone.xclone.commons.exception
import com.xclone.xclone.commons.constants.LogEvents
import jakarta.servlet.http.HttpServletRequest
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)



    @ExceptionHandler(ApiException::class)
    fun handleApi(ex: ApiException, req: HttpServletRequest): ResponseEntity<ProblemDetail> {
        logger.warn(
            "${LogEvents.API_EXCEPTION} {} {} {}",
            kv(LogFields.ERROR_CODE, ex.code.name),
            kv(LogFields.RESPONSE_STATUS, ex.code.status.value()),
            kv(LogFields.URI_PATH, req.requestURI),
            ex
        )
        val pd = ProblemDetail.forStatus(ex.code.status).apply {
            title = ex.code.name
            detail = ex.message
            setProperty("code", ex.code.name)
            setProperty("path", req.requestURI)
            MDC.get("traceId")?.let { setProperty("traceId", it) }
        }
        return ResponseEntity.status(ex.code.status).body(pd)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException, req: HttpServletRequest): ResponseEntity<ProblemDetail> {
        logger.warn(
            "${LogEvents.VALIDATION_FAILED} {} {}",
            kv(LogFields.URI_PATH, req.requestURI),
            kv(LogFields.FIELD_COUNT, ex.bindingResult.fieldErrorCount)
        )
        val pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
            title = "VALIDATION_ERROR"
            detail = "Validation failed"
            setProperty("code", "VALIDATION_ERROR")
            setProperty("path", req.requestURI)
            setProperty("fields", ex.bindingResult.fieldErrors.map {
                mapOf("field" to it.field, "message" to (it.defaultMessage ?: "invalid"))
            })
            MDC.get("traceId")?.let { setProperty("traceId", it) }
        }
        return ResponseEntity.badRequest().body(pd)
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuth(ex: AuthenticationException, req: HttpServletRequest): ResponseEntity<ProblemDetail> {
        logger.warn("${LogEvents.UNAUTHORIZED}")
        val pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED).apply {
            title = "UNAUTHORIZED"
            detail = "Authentication required"
            setProperty("code", "MISSING_OR_INVALID_AUTH")
            setProperty("path", req.requestURI)
            MDC.get("traceId")?.let { setProperty("traceId", it) }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(pd)
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException::class)
    fun handleDenied(ex: AccessDeniedException, req: HttpServletRequest): ResponseEntity<ProblemDetail> {
        logger.warn("${LogEvents.ACCESS_DENIED} {}", kv(LogFields.URI_PATH, req.requestURI))
        val pd = ProblemDetail.forStatus(HttpStatus.FORBIDDEN).apply {
            title = "FORBIDDEN"
            detail = "Not allowed"
            setProperty("code", "FORBIDDEN")
            setProperty("path", req.requestURI)
            MDC.get("traceId")?.let { setProperty("traceId", it) }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(pd)
    }

    @ExceptionHandler(Exception::class)
    fun handleAny(ex: Exception, req: HttpServletRequest): ResponseEntity<ProblemDetail> {
        logger.error(
            "${LogEvents.UNHANDLED_EXCEPTION} {}",
            kv(LogFields.URI_PATH, req.requestURI),
            ex
        )

        if (
            req.requestURI.startsWith("/v3/api-docs") ||
            req.requestURI.startsWith("/swagger-ui")
        ) {
            throw ex
        }

        val pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR).apply {
            title = "INTERNAL_ERROR"
            detail = "Unexpected error"
            setProperty("code", "INTERNAL_ERROR")
            setProperty("path", req.requestURI)
            MDC.get("traceId")?.let { setProperty("traceId", it) }
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(pd)
    }
}