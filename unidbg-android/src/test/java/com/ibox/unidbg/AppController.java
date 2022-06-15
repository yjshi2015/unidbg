package com.ibox.unidbg;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AppController {

    TigerTallyAPI tigerTallyAPI = new TigerTallyAPI();

    @RequestMapping("/encrypt")
    public Map<String, String> encrypt(String reqbody) {
        String wtoken = tigerTallyAPI.encrypt(reqbody);
        Map<String, String> map = new HashMap<>();
        map.put("wtoken", wtoken);
        return map;
    }
}
