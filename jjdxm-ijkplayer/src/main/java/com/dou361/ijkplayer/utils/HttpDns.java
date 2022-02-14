package com.dou361.ijkplayer.utils;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

//import cn.xxt.commons.util.DebugUtil;
//import cn.xxt.commons.util.LogUtil;
//import cn.xxt.commons.util.StringUtil;
import okhttp3.Cache;
import okhttp3.Dns;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * [文件描述]
 *
 * @author Luke
 * @date 2018/7/5 上午10:26
 */
public class HttpDns implements Dns {

    private static OkHttpClient httpDnsClient;

    private Context context;

    private static HttpDns instance;

    private HttpDns(Context context) {
        this.context = context;
    }

    /**
     * 单例函数
     * @param context 上下文
     * @return 单例对象
     */
    public static synchronized HttpDns getInstance(Context context) {
        if (instance == null) {
            instance = new HttpDns(context);
        }
        return instance;
    }

    @Override
    public List<InetAddress> lookup (String hostname) throws UnknownHostException {
        //防御代码
        if (hostname == null) {
            throw new UnknownHostException("hostname == null");
        }

//        if (DebugUtil.isCanDebug()) {
//            String ip = DebugUtil.getIpFromAppHostSetList(hostname);
//            if (!StringUtil.isEmpty(ip)) {
//                return Arrays.asList(InetAddress.getAllByName(ip));
//            }
//        }

        //dnspod提供的dns服务
        HttpUrl httpUrl = new HttpUrl.Builder().scheme("http")
                .host("119.29.29.29")
                .addPathSegment("d")
                .addQueryParameter("dn", hostname)
//                .addQueryParameter("ip", LogUtil.getPublicIp(context))
                .build();
        Request dnsRequest = new Request.Builder().url(httpUrl).get().build();
        try {
            String s = getHTTPDnsClient(context).newCall(dnsRequest).execute().body().string();
            //避免服务器挂了却无法查询DNS
            if (!s.matches("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b")) {
                return Dns.SYSTEM.lookup(hostname);
            }
            return Arrays.asList(InetAddress.getAllByName(s));
        } catch (IOException e) {
            return Dns.SYSTEM.lookup(hostname);
        }
    }


    public String getIp(String host) throws Exception {
        List<InetAddress> ipList = lookup(host);
        if (ipList!=null && ipList.size()>0) {
            return ipList.get(0).getHostAddress();
        } else {
            throw new UnknownHostException();
        }
    }

     private static OkHttpClient getHTTPDnsClient(Context context) {
        if (httpDnsClient == null) {
            synchronized (HttpDns.class) {
                if (httpDnsClient == null) {
                    final File cacheDir = context.getExternalCacheDir();
                    httpDnsClient = new OkHttpClient.Builder()
                            .addNetworkInterceptor(new Interceptor() {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    Response originalResponse = chain.proceed(chain.request());
                                    return originalResponse.newBuilder()
                                            //在返回header中加入缓存消息
                                            //下次将不再发送请求
                                            .header("Cache-Control", "max-age=7200").build();
                                }
                            })
                            //5MB的文件缓存
                            .cache(new Cache(new File(cacheDir, "httpdns"), 10 * 1024 * 1024))
                            .build();
                }
            }
        }
        return httpDnsClient;
    }

}
