package com.thanh.library.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.springframework.http.HttpHeaders;

public class LibraryHeaderUtil {

    public static HttpHeaders createAlert(String applicationName, String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-" + applicationName + "-alert", message);
        try {
            headers.add("X-" + applicationName + "-params", URLEncoder.encode(param, StandardCharsets.UTF_8.toString()));
        } catch (UnsupportedEncodingException e) {
            // StandardCharsets are supported by every Java implementation so this exception will never happen
        }
        return headers;
    }

    public static HttpHeaders createBookCopyBorrowAlert(String applicationName, boolean enableTranslation, String[] params) {
        String message = enableTranslation
            ? applicationName + ".bookCopy.borrowed"
            : "A Book Copy with identifier " + params[0] + " of Book with identifier " + params[1] + " has been borrowed";
        return createAlert(applicationName, message, Arrays.toString(params));
    }

    public static HttpHeaders createBookCopyReturnAlert(String applicationName, boolean enableTranslation, String[] params) {
        String message = enableTranslation
            ? applicationName + ".bookCopy.returned"
            : "A Book Copy with identifier " + params[0] + " of Book with identifier" + params[1] + " has been returned";
        return createAlert(applicationName, message, Arrays.toString(params));
    }

    public static HttpHeaders createBookWaitFromReservationAlert(String applicationName, boolean enableTranslation, String param) {
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
