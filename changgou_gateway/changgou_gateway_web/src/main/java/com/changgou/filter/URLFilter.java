package com.changgou.filter;

/**
 * 用户识别不需要登录的Url地址
 * @author Steven
 * @description com.changgou.filter
 */
public class URLFilter {
    //不需要登录的url
    private static final String[] ignore = {
            "/api/user/login",
            "/api/user/add"
    };

    /**
     * 识别传入的uri是否要权限校验
     * @param uri 当前传入的uri
     * @return true需要 | false不需要
     */
    public static boolean hasAuthorize(String uri){
        for (String ig : ignore) {
            //如果配置到不需要校验地址
            if (uri.startsWith(ig)) {
                return false;
            }
        }
        return true;
    }
}
