package com.codeages.framework.handler;

import com.codeages.framework.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://www.baeldung.com/global-error-handler-in-a-spring-rest-api"></a>
 */
@ControllerAdvice
public class ExceptionResponseHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ServiceException.class})
    public final ResponseEntity<ExceptionResponse> handleServiceExceptions(ServiceException e, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(e.getLocalizedMessage());

        var error = new ExceptionResponse();
        error.setCode(e.getCode());
        error.setMessage(e.getMessage());
        error.setDetails(details);
        error.setTraceId(request.getHeader("X-REQUEST-ID"));

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Throwable.class)
    public final ResponseEntity<ExceptionResponse> handleOtherExceptions(Exception e, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(e.getLocalizedMessage());

        var error = new ExceptionResponse();
        error.setCode("500");
        error.setMessage(e.getMessage());
        error.setDetails(details);
        error.setTraceId(request.getHeader("X-REQUEST-ID"));

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<Object> handleValidationFailedException(ConstraintViolationException ex,
                                                                        WebRequest req) {
        var error = new ExceptionResponse();

        var field = new ArrayList<String>();
        for (Path.Node node : ex.getConstraintViolations().iterator().next().getPropertyPath()) {
            if (node.getKind().name().equals("PROPERTY")) {
                field.add(node.getName());
            }
        }

        // 简化参数校验错误时的输出的错误提示，例如：
        // 原输出："userCreationDto.email: 邮件地址不正确"
        // 修改后输出："email: 邮件地址不正确"
        var fieldName = String.join(".", field);
        error.setCode("400");
        error.setMessage(fieldName + " " + ex.getConstraintViolations().iterator().next().getMessage());

        List<String> details = new ArrayList<>();
        details.add(ex.getLocalizedMessage());
        error.setDetails(details);
        error.setTraceId(req.getHeader("X-REQUEST-ID"));

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
