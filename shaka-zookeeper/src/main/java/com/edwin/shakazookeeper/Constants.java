package com.edwin.shakazookeeper;

/**
 * @author jinming.wu
 * @date 2015-5-11
 */
public class Constants {

    // read from property
    public static int          DEFAULT_SESSION_TIMEOUT    = Integer.getInteger("shaka-default-session-timeout",
                                                                               60 * 1000);

    public static int          DEFAULT_CONNECTION_TIMEOUT = Integer.getInteger("shaka-default-connection-timeout",
                                                                               15 * 1000);

    public static int          BASE_SLEEP_MS              = Integer.getInteger("shaka-base-sleep-ms", 2 * 1000);

    public static int          MAX_TRY_TIMES              = Integer.getInteger("shaka-max-try-times", 1);

    public static String       DEFAULT_CONNECTION         = "192.168.7.41:2181";

    public static String       CHARSET                    = "UTF-8";

    public static final String SEPARATOR                  = "/";
}
