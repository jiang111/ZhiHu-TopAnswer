package com.jiang.android.architecture.okhttp.log;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSource;

import static okhttp3.internal.platform.Platform.INFO;

/**
 * Created by jiang on 2016/10/11.
 */

public class HttpLoggingInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    public enum Level {
        /**
         * No logs.
         */
        NONE,
        /**
         * Logs request and response lines.
         * <p>
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1 (3-byte body)
         *
         * <-- 200 OK (22ms, 6-byte body)
         * }</pre>
         */
        BASIC,
        /**
         * Logs request and response lines and their respective headers.
         * <p>
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * <-- END HTTP
         * }</pre>
         */
        HEADERS,
        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         * <p>
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * Hi?
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * Hello!
         * <-- END HTTP
         * }</pre>
         */
        BODY
    }

    public interface Logger {
        void log(String message, String json);


        /**
         * A {@link Logger} defaults output appropriate for the current platform.
         */
        Logger DEFAULT = new Logger() {
            @Override
            public void log(String message, String json) {
                Platform.get().log(INFO, message, null);
            }

        };
    }

    public HttpLoggingInterceptor() {
        this(Logger.DEFAULT);
    }

    public HttpLoggingInterceptor(Logger logger) {
        this.logger = logger;
    }

    private final Logger logger;

    private volatile Level level = Level.NONE;

    /**
     * Change the level at which this interceptor logs.
     */
    public HttpLoggingInterceptor setLevel(Level level) {
        if (level == null) throw new NullPointerException("level == null. Use Level.NONE instead.");
        this.level = level;
        return this;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Level level = this.level;

        Request request = chain.request();
        if (level == Level.NONE) {
            return chain.proceed(request);
        }
        StringBuilder logBuilder = new StringBuilder();
        String responseJson = null;
        logBuilder.append("\n").append("------------request--------------").append("\n");
        boolean logBody = level == Level.BODY;
        boolean logHeaders = logBody || level == Level.HEADERS;

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;

        logBuilder.append("url: ").append(request.url()).append("\n").append("method: ").append(request.method())
                .append("\n").append("protocol: ").append(protocol).append("\n");

        if (!logHeaders && hasRequestBody) {
            logBuilder.append(" (" + requestBody.contentLength() + "-byte body)");
        }
        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody.contentType() != null) {
                    logBuilder.append("Content-Type: " + requestBody.contentType()).append("\n");
                }
                if (requestBody.contentLength() != -1) {
                    logBuilder.append("Content-Length: " + requestBody.contentLength()).append("\n");
                }
            }

            Headers headers = request.headers();
            logBuilder.append("--------headers----------").append("\n");
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    logBuilder.append(name + ": " + headers.value(i)).append("\n");
                }
            }
            if (!logBody || !hasRequestBody) {
                logBuilder.append("------------end--------------").append("\n");
            } else if (bodyEncoded(request.headers())) {
                logBuilder.append("------------end--------------" + " (encoded body omitted)").append("\n");
            } else {
                logBuilder.append("--------body----------").append("\n");
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                if (isPlaintext(buffer)) {
                    logBuilder.append(buffer.readString(charset)).append("\n");
                    logBuilder.append(" (" + requestBody.contentLength() + "-byte body)").append("\n");
                } else {
                    logBuilder.append(" (binary "
                            + requestBody.contentLength() + "-byte body omitted)").append("\n");
                }
            }
        }

        logBuilder.append("\n").append("------------response--------------").append("\n");
        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            logBuilder.append("HTTP FAILED: " + e).append("\n");
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";

        logBuilder.append("code: ").append(response.code()).append("\n")
                .append("message: ").append(response.message()).append("\n")
                .append("time: ").append(tookMs).append("ms \n")
                .append(!logHeaders ? "bodySize: "
                        + bodySize : "").append("\n");

        if (logHeaders) {
            Headers headers = response.headers();
            logBuilder.append("--------headers----------").append("\n");
            for (int i = 0, count = headers.size(); i < count; i++) {
                logBuilder.append(headers.name(i) + ": " + headers.value(i)).append("\n");
            }

            if (!logBody || !HttpHeaders.hasBody(response)) {
                logBuilder.append("------------end--------------").append("\n");
            } else if (bodyEncoded(response.headers())) {
                logBuilder.append("------------end--------------(encoded body omitted)").append("\n");
            } else {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8);
                    } catch (UnsupportedCharsetException e) {
                        logBuilder.append("Couldn't decode the response body; charset is likely malformed.").append("\n");
                        logBuilder.append("------------end--------------").append("\n");
                        logger.log(logBuilder.toString(), null);
                        return response;
                    }
                }

                if (!isPlaintext(buffer)) {
                    logBuilder.append("(binary " + buffer.size() + "-byte body omitted)").append("\n");
                    logBuilder.append("------------end--------------").append("\n");
                    return response;
                }

                if (contentLength != 0) {
                    responseJson = buffer.clone().readString(charset);
                }

            }
        }
        logger.log(logBuilder.toString(), responseJson);
        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }
}
