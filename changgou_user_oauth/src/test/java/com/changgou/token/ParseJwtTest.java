package com.changgou.token;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

/*****
 * @Author: Steven
 * @Date: 2019/7/7 13:48
 * @Description: com.changgou.token
 *  使用公钥解密令牌数据
 ****/
public class ParseJwtTest {

    /***
     * 校验令牌
     */
    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MjAwMDQ1NzU1MSwiYXV0aG9yaXRpZXMiOlsidXNlciIsInNhbGVzbWFuIl0sImp0aSI6IjRhYzRhY2Y3LWY5ODEtNDIzMC1hM2EyLTZkNzVmMzRlM2Y2YiIsImNsaWVudF9pZCI6ImNoYW5nZ291IiwidXNlcm5hbWUiOiJ6aGFuZ3NhbiJ9.GMyZiWbpGYot976CxAnJiiap5ZkhWCO4ODPhh-TYmInl0v-vUu1q-wkeZS-tHTkMZKVPwH-A5CfgDpTBXkechGqMHPqn1OKuJKkBOkW0vLKIhtnpdcBkQwVgMN1K14zDpV9cqbC5OmymN3mQPjIu_fP3wp0yPGgtSt7LApNXkPeqS3dVN58wck_RIMPQq-CWFln2KpBk39F0nnqi-hD6dqD9qRIHU4bTOI44XJ0OMu8YRgfktiKm0ls2oqkaKwMqGvEgrronySwdyYpyrF9gLZ5HpLiuDz5R9q63Nq2yi9EZCgT2fThteWRx35MSbGykDGGRWriFxLhbkLpuYU-fQA";

        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhLitKYszI5AIviYG7gPajCeh27hnmjy8ai4rPwJgx+8BLqgD2iAjoVDx9HvnezwVqdVADo+Lcrb8a+ydkXTS8c59NvudCjHyf+/fMKgfiF4eBANsiZ8dDdmfqoRqycFbfFAvpnAFuWx287DzVErQaMZKl6lRSdKqxzqESHgQ1g1W21cTUB+isyNdmGflYUJc5M1vLptfJbPBx5kK42t2/gov14QNXahEEDVSecf1NJDMOXjbYgUTMs3fboPPsvZ6C0nX5qCiEgZypBaKvL3w/6/8cfRoiAcgCVxTrQyuT96Yf5y9oClQiLB+C7zbnx2Jr+G/SVgbftQ0IrsFlhns8wIDAQAB-----END PUBLIC KEY-----";

        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
