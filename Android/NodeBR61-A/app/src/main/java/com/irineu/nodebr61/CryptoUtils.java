package com.irineu.nodebr61;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.util.Collection;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class CryptoUtils {

    final private static boolean TRUST_IN_ANY = false;

    private String host = "192.168.226.92";

    private InputStream trustedCertificatesInputStream() {
        String entrustRootCertificateAuthority = "" +
                "-----BEGIN CERTIFICATE-----\n" +
                "MIIEQjCCAyoCCQCoWYT5I5tStDANBgkqhkiG9w0BAQsFADBjMQswCQYDVQQGEwJi\n" +
                "cjELMAkGA1UECAwCc3AxCzAJBgNVBAcMAnNwMQ8wDQYDVQQKDAZpcmluZXUxEDAO\n" +
                "BgNVBAsMB2FudHVuZXMxFzAVBgNVBAMMDjE5Mi4xNjguMjI2LjkyMB4XDTIzMDMw\n" +
                "MjAxNTUwM1oXDTI0MDMwMTAxNTUwM1owYzELMAkGA1UEBhMCYnIxCzAJBgNVBAgM\n" +
                "AnNwMQswCQYDVQQHDAJzcDEPMA0GA1UECgwGaXJpbmV1MRAwDgYDVQQLDAdhbnR1\n" +
                "bmVzMRcwFQYDVQQDDA4xOTIuMTY4LjIyNi45MjCCAiIwDQYJKoZIhvcNAQEBBQAD\n" +
                "ggIPADCCAgoCggIBALc1tKoyvFj0F5jKqepP4YrXJYIOzKNm61ue9y6QrUNDdkV3\n" +
                "qLizfFWYcLZLDGybjlUmbip4Kv2gl35751aTOeuQ3QxJzv0zCDOTAFqt+1ZaNYD8\n" +
                "2J6zeAldMPz/PCuYn6X9imG60rFe/ebdXaJBYztGwrJl4UIv02YiH4oZKU/2em1W\n" +
                "0ODFJA9uTPZpFfFpQaXkCidy26XTwQ1yKsMP77xdcsSqc0s2j6lNeQ4Dgc+c4Z5G\n" +
                "XPyFcYmBc2wF4AZ/WxDw+XrNGjt+BO2HOnUWj+w/yYO92dIgGxwz5jq+gmlz68xp\n" +
                "HcNHf/kweMLFU3CXwzhHsko25cfBWaE6au4ZcWwU+DNtmowzJJAVXsQ0CjEv86QG\n" +
                "0PHH0unSjJgA2qBL8MweciQQJLpBEnpcx8eHWJIXnKOw7wK94PXRBgPuIZ7benq8\n" +
                "2G9cM33DxtUuUs4UsCLIgb4SLIo/0kYzeOzVoQa5sof0igMurgGQ9wCDTLa7EJ1u\n" +
                "KOR9b3WoTXZjwNNOR4rwc1AgY3Tt2hgnGP9IQAqh463LBnNytq46LsGDGZkqWkmQ\n" +
                "fV08LPyxkrJ5Xe1u1rqtoBXzdWgUMDNH/ZBdYFoHoY2SS3JZMixEjV7kCeKP58w/\n" +
                "E48a4WLvK8xcOAPUX8vs1Mp2cH97ELjBweXvY/7stQ/2I/DZ8vtnpWNjlx4PAgMB\n" +
                "AAEwDQYJKoZIhvcNAQELBQADggEBAJORk83pi+STwSurRwEjccUk8eTGsJFibEYD\n" +
                "FRkWZUT+PwH5lOM8LqoTXcXMfL4W3C0T/4hBu98FeKGQbhpPDbXrXjzg+vjWOU0z\n" +
                "SWi33iFOE9wTpJUfmBpTifYtI/xfCwUawljo3Z0mJVwQCIT0YFMaZf9lS1IyzkoU\n" +
                "uyqM63PgHCtyc7BgPke3ycloz9QdPyO8KSR5BH31uNaBU88A4Gk/hlHt2F2kJhty\n" +
                "K+0UmWwDtEhSKepcKiR7jY6WxNOftZwsEAsDuHNQNh+610SdTuXmIWhzSWr3+LrW\n" +
                "V4lLOlCVA3R4UgiM4XjrQ9F8oqPFl5jeCIQ3jQ/QhmU77McD+7k=\n" +
                "-----END CERTIFICATE-----";
        return new Buffer()
                .writeUtf8(entrustRootCertificateAuthority)
                .inputStream();
    }


    private TrustManager[] trustCerts = new TrustManager[]{
            //new X509TrustManager() {
            new X509ExtendedTrustManager() {

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {

                    if(!chain[0].equals(getAcceptedIssuers()[0])){
                        throw new CertificateException();
                    }

                    if (socket instanceof SSLSocket) {
                        SSLSocket sslSocket = (SSLSocket) socket;
                        SSLSession session = sslSocket.getHandshakeSession();
                        if (session == null) {
                            throw new CertificateException("Not in handshake; no session available");
                        }

                        String host = session.getPeerHost();

                        Properties prop = new Properties();
                        try {
                            prop.load(new StringReader(chain[0].getSubjectDN().getName().replaceAll(",", "\n")));
                            String CN= prop.getProperty("CN");

                            if(!host.equalsIgnoreCase(CN)){
                                throw new CertificateException();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {

                }

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    if(getAcceptedIssuers().length == 0){
                        return;
                    }
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {

                    if(!TRUST_IN_ANY){
                        try {
                            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                            Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(trustedCertificatesInputStream());
                            return new java.security.cert.X509Certificate[]{ (java.security.cert.X509Certificate) certificates.toArray()[0]};
                        } catch (CertificateException e) {
                            e.printStackTrace();
                        }
                    }

                    return new java.security.cert.X509Certificate[]{};
                }
            }
    };

    OkHttpClient getHTTPSClient() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("SSL");

        sslContext.init(null, trustCerts, new java.security.SecureRandom());

        OkHttpClient.Builder newBuilder = new OkHttpClient.Builder();
        newBuilder.sslSocketFactory(sslContext.getSocketFactory());
        newBuilder.hostnameVerifier((hostname, session) -> true);

        OkHttpClient httpsClient = newBuilder.build();

        return httpsClient;
    }

    private String getToken(String payload) throws GeneralSecurityException, IOException {

        OkHttpClient httpsClient = getHTTPSClient();

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), payload);

        Request request = new Request.Builder()
                .url("https://"+host+":4000/gentoken")
                .post(body)
                .build();

        Response response = httpsClient.newCall(request).execute();

        return response.body().string().replaceAll("\"", "");
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public byte[] enc(String payload) throws GeneralSecurityException, IOException {
        byte[] key = genKey(payload);
        byte[] iv = getIV(key);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
        byte [] result = cipher.doFinal(payload.getBytes(StandardCharsets.UTF_8));

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(result);

        return md.digest();
    }

    public String checkRemoteKey(String payload) throws Exception {

        OkHttpClient httpsClient = getHTTPSClient();

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), payload);

        Request request = new Request.Builder()
                .url("https://"+host+":4000/check")
                .post(body)
                .build();

        Response response = httpsClient.newCall(request).execute();

        if(response.code() != 200){
            throw new Exception("Invalid key");
        }

        return response.body().string();
    }

    public byte [] genKey(String payload) throws GeneralSecurityException, IOException {

        String token = getToken(payload);

        byte [] salt = getSalt();

        String algorithm = "PBKDF2WithHmacSHA256";
        int derivedKeyLength = 32 * 8;
        int iterations = 2000;

        KeySpec spec1 = new PBEKeySpec(token.toCharArray(), salt, iterations, derivedKeyLength);
        SecretKeyFactory fac1 = SecretKeyFactory.getInstance(algorithm);

        return fac1.generateSecret(spec1).getEncoded();
    }

    private native byte[] getSalt();
    public native byte[] getIV(byte[] key);
}
