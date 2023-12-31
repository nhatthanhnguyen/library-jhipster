package com.thanh.library.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;

public class LibraryHeaderUtil {

    public static HttpHeaders createAlert(String applicationName, String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-" + applicationName + "-alert", message);
        headers.add("X-" + applicationName + "-params", URLEncoder.encode(param, StandardCharsets.UTF_8));
        return headers;
    }

    public static HttpHeaders createBookCopyBorrowAlert(String applicationName, boolean enableTranslation, String param) {
        String message = enableTranslation
            ? applicationName + ".bookCopy.borrowed"
            : "A Book Copy with identifier " + param + " has been borrowed";
        return createAlert(applicationName, message, param);
    }

    public static HttpHeaders createBookCopyReturnAlert(
        String applicationName,
        boolean enableTranslation,
        String param,
        boolean isReturned
    ) {
        String message = enableTranslation
            ? isReturned ? applicationName + ".bookCopy.returnedSuccessfully" : applicationName + ".bookCopy.returnedFailed"
            : "A Book Copy with identifier " + param + "has been returned";
        return createAlert(applicationName, message, param);
    }

    public static HttpHeaders createBookWaitAlert(String applicationName, boolean enableTranslation, String param) {
        String message = enableTranslation
            ? applicationName + ".book.addToQueueSuccessfully"
            : "A Book with identifier " + param + " has been add to queue";
        return createAlert(applicationName, message, param);
    }

    public static HttpHeaders createBookHoldAlert(String applicationName, boolean enableTranslation, String param) {
        String message = enableTranslation ? applicationName + ".bookCopy.hold" : "A Book with identifier " + param + " has bean hold";
        return createAlert(applicationName, message, param);
    }

    public static HttpHeaders createEntityRestoreAlert(String applicationName, boolean enableTranslation, String entityName, String param) {
        String message = enableTranslation
            ? applicationName + "." + entityName + ".restored"
            : "A " + entityName + " is restored with identifier " + param;
        return createAlert(applicationName, message, param);
    }
}
