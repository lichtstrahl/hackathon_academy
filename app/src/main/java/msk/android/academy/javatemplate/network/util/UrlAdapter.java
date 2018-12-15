package msk.android.academy.javatemplate.network.util;

public class UrlAdapter {
    private UrlAdapter() {}

    public static String adapt(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        return url;
    }
}
