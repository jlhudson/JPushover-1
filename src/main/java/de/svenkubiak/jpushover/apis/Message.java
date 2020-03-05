package de.svenkubiak.jpushover.apis;

import java.io.IOException;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

import de.svenkubiak.jpushover.enums.Param;
import de.svenkubiak.jpushover.enums.Priority;
import de.svenkubiak.jpushover.enums.Sound;
import de.svenkubiak.jpushover.http.PushoverRequest;
import de.svenkubiak.jpushover.http.PushoverResponse;
import de.svenkubiak.jpushover.utils.Urls;

/**
 * 
 * @author svenkubiak
 *
 */
public class Message {
    private static final String MESSAGE_URL = Urls.getMessageUrl();
    private static final String VALIDATION_URL = Urls.getValidationUrl();
    private Priority priority = Priority.NORMAL;
    private Sound sound = Sound.PUSHOVER;
    private String token;
    private String user;
    private String message;
    private String device;
    private String title;
    private String url = "";
    private String urlTitle = "";
    private String callback;
    private String proxyHost;
    private int proxyPort;
    private int retry;
    private int expire;
    private int timestamp;
    private boolean html;
    private boolean monospace;
    
    public Message() {
    }
    
    /**
     * Your application's API token
     * (required)
     *
     * @param token The pushover API token
     * @return Message instance
     */
    public final Message withToken(final String token) {
        Objects.requireNonNull(token, "Token can not be null");
        
        this.token = token;
        return this;
    }

    /**
     * The user/group key (not e-mail address) of your user (or you),
     * viewable when logged into the @see <a href="https://pushover.net/login">pushover dashboard</a>
     * (required)
     *
     * @param user The username
     * @return Message instance
     */
    public final Message withUser(final String user) {
        this.user = user;
        return this;
    }

    /**
     * Specifies how often (in seconds) the Pushover servers will send the same notification to the user.
     * Only required if priority is set to emergency.
     *
     * @param retry Number of seconds
     * @return Message instance
     */
    public final Message withRetry(final int retry) {
        this.retry = retry;
        return this;
    }

    /**
     * Specifies how many seconds your notification will continue to be retried for (every retry seconds).
     * Only required if priority is set to emergency.
     *
     * @param expire Number of seconds
     * @return Message instance
     */
    public final Message withExpire(final int expire) {
        this.expire = expire;
        return this;
    }

    /**
     * Your message
     * (required)
     *
     * @param message The message to sent
     * @return Message instance
     */
    public final Message withMessage(final String message) {
        this.message = message;
        return this;
    }

    /**
     * Your user's device name to send the message directly to that device,
     * rather than all of the user's devices
     * (optional)
     *
     * @param device The device name
     * @return Message instance
     */
    public final Message withDevice(final String device) {
        this.device = device;
        return this;
    }

    /**
     * Your message's title, otherwise your app's name is used
     * (optional)
     *
     * @param title The title
     * @return Message instance
     */
    public final Message withTitle(final String title) {
        this.title = title;
        return this;
    }

    /**
     * A supplementary URL to show with your message
     * (optional)
     *
     * @param url The url
     * @return Message instance
     */
    public final Message withUrl(final String url) {
        this.url = url;
        this.urlTitle = url;
        return this;
    }
    
    /**
     * Enables HTML in the pushover message
     * Either HTML or monospace can be enabled
     * (optional)
     *
     * @return Message instance
     */
    public final Message enableMonospace() {
        this.monospace = true;
        this.html = false;
        return this;
    }
    
    /**
     * Enables HTML in the pushover message
     * Either HTML or monospace can be enabled
     * (optional)
     *
     * @return Message instance
     */
    public final Message enableHtml() {
        this.monospace = false;
        this.html = true;
        return this;
    }

    /**
     * A title for your supplementary URL, otherwise just the URL is shown
     *
     * @param urlTitle The url title
     * @return Message instance
     */
    public final Message withUrlTitle(final String urlTitle) {
        this.urlTitle = urlTitle;
        return this;
    }

    /**
     * A Unix timestamp of your message's date and time to display to the user,
     * rather than the time your message is received by our API
     *
     * @param timestamp The Unix timestamp
     * @return Message instance
     */
    public final Message withTimestamp(final int timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * Priority of the message based on the @see <a href="https://pushover.net/api#priority">documentation</a>
     * (optional)
     *
     * @param priority The priority enum
     * @return Message instance
     */
    public final Message withPriority(final Priority priority) {
        this.priority = priority;
        return this;
    }

    /**
     * The name of one of the sounds supported by device clients to override
     * the user's default sound choice
     * (optional)
     *
     * @param sound THe sound enum
     * @return Message instance
     */
    public final Message withSound(final Sound sound) {
        this.sound = sound;
        return this;
    }

    /**
     * Callback parameter may be supplied with a publicly-accessible URL that the
     * pushover servers will send a request to when the user has acknowledged your
     * notification.
     * Only required if priority is set to emergency.
     *
     * @param callback The callback URL
     * @return Message instance
     */
    public final Message withCallback(final String callback) {
        this.callback = callback;
        return this;
    }

    /**
     * Uses the given proxy for HTTP requests
     *
     * @param proxyHost The host that should be used for the Proxy
     * @param proxyPort The port that should be used for the Proxy
     * @return Message instance
     */
    public final Message withProxy(final String proxyHost, final int proxyPort) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        return this;
    }

    /**
     * Sends a validation request to pushover ensuring that the token and user
     * is correct, that there is at least one active device on the account.
     *
     * Requires token parameter
     * Requires user parameter
     * Optional device parameter to check specific device
     *
     * @return true if token and user are valid and at least on device is on the account, false otherwise
     *
     * @throws IOException if validation fails
     * @throws InterruptedException if validation fails
     */
    public boolean validate() throws IOException, InterruptedException {
        Objects.requireNonNull(this.token, "Token is required for validation");
        Objects.requireNonNull(this.user, "User is required for validation");

        NavigableMap<String, String> body = new TreeMap<>();
        body.put(Param.TOKEN.toString(), this.token);
        body.put(Param.USER.toString(), this.user);

        var pushoverResponse = new PushoverRequest().push(VALIDATION_URL, body, this.proxyHost, this.proxyPort);
        
        var valid = false;
        if (pushoverResponse.getHttpStatus() == 200) {
            var response = pushoverResponse.getResponse();
            if (response != null && response.contains("\"status\":1")) {
                valid = true;
            }
        }

        return valid;
    }

    /**
     * Sends a message to pushover
     *
     * @return PushoverResponse instance
     *
     * @throws IOException if sending the message fails
     * @throws InterruptedException if sending the message fails
     */
    public final PushoverResponse push() throws IOException, InterruptedException {
        Objects.requireNonNull(this.token, "Token is required for a message");
        Objects.requireNonNull(this.user, "User is required for a message");
        Objects.requireNonNull(this.message, "Message is required for a message");
        
        if (Priority.EMERGENCY.equals(this.priority)) {
            if (this.retry == 0) {
                this.retry = 60;
            }
            
            if (this.expire == 0) {
                this.expire = 3600;
            }
        }
        
        if (this.message.length() > 1024) {
            this.message.substring(0, 1023);
        }
        
        if (this.title != null && this.title.length() > 250) {
            this.title.substring(0, 249);
        }
        
        if (this.url != null && this.url.length() > 512) {
            this.url.substring(0, 511);
        }
        
        if (this.urlTitle != null && this.urlTitle.length() > 100) {
            this.urlTitle.substring(0, 99);
        }
        
        NavigableMap<String, String> body = new TreeMap<>();
        body.put(Param.TOKEN.toString(), this.token);
        body.put(Param.USER.toString(), this.user);
        body.put(Param.MESSAGE.toString(), this.message);
        body.put(Param.URL.toString(), this.url);
        body.put(Param.URL_TITLE.toString(), this.urlTitle);
        body.put(Param.PRIORITY.toString(), this.priority.toString());
        body.put(Param.SOUND.toString(), this.sound.toString());
        body.put(Param.HTML.toString(), this.html ? "1" : "0");
        body.put(Param.MONOSPACE.toString(), this.monospace ? "1" : "0");
        
        if (this.device != null) {
            body.put(Param.DEVICE.toString(), this.device); 
        }
        
        if (this.title != null) {
            body.put(Param.TITLE.toString(), this.title); 
        }
        
        if (this.callback != null) {
            body.put(Param.CALLBACK.toString(), this.callback);
        }
        
        if (this.timestamp > 0) {
            body.put(Param.TIMESTAMP.toString(), String.valueOf(this.timestamp));
        }
        
        if (this.retry > 0) {
            body.put(Param.RETRY.toString(), String.valueOf(this.retry));
        }

        if (this.expire > 0) {
            body.put(Param.EXPIRE.toString(), String.valueOf(this.expire)); 
        }
        
        return new PushoverRequest().push(MESSAGE_URL, body, this.proxyHost, this.proxyPort);
    }

    public Priority getPriority() {
        return priority;
    }

    public Sound getSound() {
        return sound;
    }

    public String getToken() {
        return token;
    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public String getDevice() {
        return device;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlTitle() {
        return urlTitle;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public int getRetry() {
        return retry;
    }

    public int getExpire() {
        return expire;
    }

    public String getCallback() {
        return callback;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public boolean isHtml() {
        return html;
    }

    public boolean isMonospace() {
        return monospace;
    }
}