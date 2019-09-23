package com.changgou.search.controller;

import com.changgou.search.feign.SkuFeign;
import com.changgou.search.pojo.SkuInfo;
import entity.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author Steven
 * @version 1.0
 * @description com.changgou.search.controller
 * @date 2019-9-9
 */
@Controller
@RequestMapping("search")
public class SkuController {
    @Autowired
    private SkuFeign skuFeign;

    /**
     * @param searchMap
     * 注意此处的@GetMapping()要添加list的url请求，不然会跟SkuFeign中的请求url冲突
     * @return
     */
    @GetMapping("list")
    public String search(@RequestParam(required = false) Map searchMap, Model model){
        //查询数据
        Map result = skuFeign.search(searchMap);
        model.addAttribute("result", result);
        //返回查询条件
        model.addAttribute("searchMap", searchMap);

        //返回拼接好的url
        String url = this.getUrl(searchMap);
        model.addAttribute("url", url);

        //返回分页对象
        Page<SkuInfo> page = new Page<SkuInfo>(
                new Long(result.get("total").toString()),
                new Integer(result.get("pageNum").toString()),
                new Integer(result.get("pageSize").toString())
        );
        model.addAttribute("page", page);
        return "search";
    }

    /**
     * 把Map转换成/search/list?key=value&key2=value2
     * @param searchMap
     * @return
     */
    private String getUrl(Map<String,Object> searchMap){
        String url = "/search/list";
        if(searchMap != null){
            url += "?";
            for (String key : searchMap.keySet()) {
                ////如果是排序的参数，不拼接到url上，便于下次换种方式排序
                if(key.indexOf("sort") > -1 || key.equals("pageNum")){
                    continue;
                }
                url += key + "=" + searchMap.get(key) + "&";
            }
            url = url.substring(0,url.length() - 1);
        }
        return url;
    }
}
