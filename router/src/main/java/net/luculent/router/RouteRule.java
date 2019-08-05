package net.luculent.router;

import android.net.Uri;
import android.os.Bundle;

import java.util.Set;


/**
 * Created by xiayanlei on 2017/2/6.
 */
public class RouteRule {

    //url标准格式- scheme://host:port/path?query
    public Uri uri;
    public String scheme;//跳转协议不能为空
    public String host;
    public String path;
    public Set<String> params;
    private int count = 1;
    String component;

    public RouteRule(Uri uri) {
        this.uri = uri;
        this.scheme = uri.getScheme();
        this.host = uri.getHost();
        this.path = uri.getPath();
        this.params = UriCompact.getQueryParameterNames(uri);
        component = host.concat(path);
    }

    public Bundle generateBundle(Bundle bundle) {
        for (String param : params) {
            bundle.putString(param, uri.getQueryParameter(param));
        }
        return bundle;
    }

    public RouteRule next() {
        String[] components = {host.concat(path), path, host};
        if (count < components.length) {
            RouteRule rule = new RouteRule(uri);
            rule.component = components[count];
            rule.count = count + 1;
            return rule;
        }
        return null;
    }

    public boolean isHttp() {
        String url = uri.toString().toLowerCase();
        return url.startsWith("http://") || url.startsWith("https://");
    }

    @Override
    public String toString() {
        return "RouteRule{" +
                "host='" + host + '\'' +
                ", path='" + path + '\'' +
                ", scheme='" + scheme + '\'' +
                '}';
    }
}
