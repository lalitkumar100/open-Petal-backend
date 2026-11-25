package com.crimsonlogic.open_petal_backend.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.crimsonlogic.open_petal_backend.common.message.repository.CsvMessageRepository;
import com.crimsonlogic.open_petal_backend.common.message.service.MessageService;
import jakarta.validation.ConstraintViolationException;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.http.converter.HttpMessageNotReadableException;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        CsvMessageRepository repository = new CsvMessageRepository(
                new DefaultResourceLoader(), "classpath:messages/application-messages.csv");
        repository.loadMessages();
        handler = new GlobalExceptionHandler(new MessageService(repository));
    }

    @AfterEach
    void clearLocale() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void returnsLocalizedValidationErrorsAndBadRequestStatus() throws Exception {
        LocaleContextHolder.setLocale(Locale.forLanguageTag("hi"));
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "request");
        bindingResult.addError(new FieldError("request", "email", "internal annotation text"));
        MethodParameter parameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("validationTarget", Object.class), 0);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        var response = handler.handleMethodArgumentNotValid(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().success());
        assertEquals("COMMON_VALIDATION_FAILED", response.getBody().messageCode());
        assertEquals("मान्यकरण विफल हुआ", response.getBody().message());
        assertTrue(response.getBody().data().containsKey("email"));
        assertFalse(response.getBody().data().get("email").contains("internal"));
    }

    @Test
    void returnsNotFoundWithGenericLocalizedResponse() {
        var response = handler.handleResourceNotFound(new ResourceNotFoundException("database identifier"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("RESOURCE_NOT_FOUND", response.getBody().messageCode());
        assertFalse(response.getBody().message().contains("database"));
        assertEquals(null, response.getBody().data());
    }

    @Test
    void mapsAuthenticationAndAuthorizationFailures() {
        var authentication = handler.handleBadCredentials(new BadCredentialsException("sensitive auth detail"));
        var authorization = handler.handleAccessDenied(new AccessDeniedException("internal policy detail"));
        var applicationAuthentication = handler.handleAuthentication(new AuthenticationException("private token detail"));
        var applicationAuthorization = handler.handleAuthorization(new AuthorizationException("private role detail"));

        assertEquals(HttpStatus.UNAUTHORIZED, authentication.getStatusCode());
        assertEquals("AUTH_INVALID_CREDENTIALS", authentication.getBody().messageCode());
        assertEquals(HttpStatus.FORBIDDEN, authorization.getStatusCode());
        assertEquals("ACCESS_DENIED", authorization.getBody().messageCode());
        assertEquals(HttpStatus.UNAUTHORIZED, applicationAuthentication.getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN, applicationAuthorization.getStatusCode());
    }

    @Test
    void mapsConstraintViolationsToBadRequest() {
        var response = handler.handleConstraintViolation(new ConstraintViolationException(Set.of()));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("COMMON_VALIDATION_FAILED", response.getBody().messageCode());
        assertTrue(response.getBody().data().isEmpty());
    }

    @Test
    void mapsDuplicateMalformedAndUnsupportedRequestsToCorrectStatuses() {
        var duplicate = handler.handleDuplicateResource(new DuplicateResourceException("private duplicate detail"));
        var malformed = handler.handleUnreadableMessage(new HttpMessageNotReadableException("private parse detail", null));
        var unsupported = handler.handleMethodNotSupported(new HttpRequestMethodNotSupportedException("TRACE"));

        assertEquals(HttpStatus.CONFLICT, duplicate.getStatusCode());
        assertEquals("DUPLICATE_RESOURCE", duplicate.getBody().messageCode());
        assertEquals(HttpStatus.BAD_REQUEST, malformed.getStatusCode());
        assertEquals("COMMON_BAD_REQUEST", malformed.getBody().messageCode());
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, unsupported.getStatusCode());
        assertEquals("COMMON_METHOD_NOT_ALLOWED", unsupported.getBody().messageCode());
    }

    @Test
    void hidesUnexpectedExceptionDetailsAndUsesServerErrorStatus() {
        var response = handler.handleUnexpected(new IllegalStateException("database password leaked"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("COMMON_ERROR", response.getBody().messageCode());
        assertEquals("Something went wrong", response.getBody().message());
        assertFalse(response.getBody().message().contains("database"));
        assertEquals(null, response.getBody().data());
    }

    @Test
    void returnsBusinessFailureWithBadRequestStatus() {
        var response = handler.handleBusiness(new BusinessException("internal business detail"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("COMMON_BUSINESS_ERROR", response.getBody().messageCode());
    }

    private void validationTarget(Object ignored) {
    }
}
