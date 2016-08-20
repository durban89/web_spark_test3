/**
 * Created by durban126 on 16/5/14.
 */

import com.alibaba.fastjson.TypeReference;
import spark.ModelAndView;
import spark.Spark;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.io.BufferedReader;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonParseException;
//import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

import com.alibaba.fastjson.JSON;
import com.github.kevinsawicki.http.HttpRequest;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {
        staticFileLocation("/public");

        Spark.exception(Exception.class, (exception, request, response) -> {
            exception.printStackTrace();
        });

        before((req, res) -> {
            boolean authenticated = false;

            if (authenticated) {
                halt(401, "go away!");
            }
        });


        after((req, res) -> {
            res.header("foo", "set by after filter");
        });

        get("/hello", (req, res) -> "Hello World");

        get("/", (req, res) -> "Home");

        get("/blog", (req, res) -> "Blog");

        //加密解密测试
        get("/encrypt", "application/json", (req, res) -> {
            String data = "aaaaaa";
            //加载私钥
            SDKUtil.doLoadPrivateKey();
            PrivateKey privateKey = SDKUtil.getPrivateKey();
            String encryptData = SDKUtil.encrypt(privateKey, data);
            String signature = SDKUtil.generateSignature(encryptData, privateKey);

            HashMap<String, String> map = new HashMap<>();

            map.put("data", encryptData);
            map.put("sign", signature);

            HttpRequest request = HttpRequest.post("http://123.57.152.189:8895/wlcapi/general2/KDJZ/productInfo.html");
            request.part("data", encryptData);
            request.part("sign", signature);

            if (request.ok()) {
                BufferedReader reader = request.bufferedReader();
                StringBuffer strBuffer = new StringBuffer();

                String inputLine;
                try {
                    while ((inputLine = reader.readLine()) != null) {
                        strBuffer.append(inputLine);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }

                reader.close();

                System.out.println(strBuffer.toString());
                System.out.println(JSON.parseObject(strBuffer.toString()));
                HashMap<String, String> jsonMap = JSON.parseObject(strBuffer.toString(), new HashMap<String, String>().getClass());
                System.out.println(jsonMap);

                for (String key : jsonMap.keySet()) {
                    String str = jsonMap.get(key);
                    System.out.println(key + ":" + str);
                }

                System.out.println("Status was updated");
            }

            String jsonString = JSON.toJSONString(map);

            return jsonString;
        });

        get("/blog/:name", (req, res) -> "Hello: " + req.params("name"));

        get("/say/*/to/*", (req, res) -> "Numbers of of splat parameters: " + req.splat().length);

        Map map = new HashMap();
        map.put("name", "sam");

        get("/handle",
                (req, res) -> new ModelAndView(map, "handle.hbs"),
                new HandlebarsTemplateEngine());
    }
}
