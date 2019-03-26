package com.wjyt.myapplication;

import java.util.HashMap;
import java.util.Map;

public class TranslateAPI {
    private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";

    private static final String APP_ID = "20190321000279798";
    private static final String SECURITY_KEY = "noAdhTFcEUU349oGX1ps";

    private String appid;
    private String securityKey;

    public TranslateAPI() {
        this.appid = APP_ID;
        this.securityKey = SECURITY_KEY;
    }


    public String getTransResult(String query, String from, String to) {
        Map<String, String> params = buildParams(query, from, to);
        return GetHttp.get(TRANS_API_HOST, params);
    }

    private Map<String, String> buildParams(String query, String from, String to) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);
        params.put("appid", appid);

        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        // 签名
        String src = appid + query + salt + securityKey; // 加密前的原文
        params.put("sign", MD5.md5(src));
        return params;
    }
}

