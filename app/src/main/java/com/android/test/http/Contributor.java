package com.android.test.http;

/**
 * des:
 * author: libingyan
 * Date: 18-5-31 20:30
 */
public class Contributor {
    public final String login;
    public final int contributions;

    public Contributor(String login, int contributions) {
        this.login = login;
        this.contributions = contributions;
    }
}
