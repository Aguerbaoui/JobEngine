package io.je.project.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.je.utilities.beans.JEResponse;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;

/*
* Spring global exception handler
* */
@ControllerAdvice
public class SpringGlobalExceptionHandler {

    //StandardServletMultipartResolver
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<?> handleError1(MultipartException e, RedirectAttributes redirectAttributes) {

        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new JEResponse(ResponseCodes.ERROR_IMPORTING_FILE, JEMessages.ERROR_IMPORTING_FILE));
    }

    //CommonsMultipartResolver
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleError2(MaxUploadSizeExceededException e, RedirectAttributes redirectAttributes, HttpServletRequest request,
                                          HttpServletResponse response) {

        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new JEResponse(ResponseCodes.FILE_TOO_LARGE_EXCEPTION, JEMessages.FILE_TOO_LARGE));

    }
}
