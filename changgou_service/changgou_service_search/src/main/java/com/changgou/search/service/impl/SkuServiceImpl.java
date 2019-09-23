package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import entity.Result;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 商品信息业务逻辑实现
 * @author Steven
 * @version 1.0
 * @description com.changgou.search.service.impl
 * @date 2019-9-6
 */
@Service
public class SkuServiceImpl implements SkuService {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private SkuEsMapper skuEsMapper;

    @Override
    public void importSku() {
        //先通过feign查询正常的sku列表-{name:steven}
        Result<List<Sku>> result = skuFeign.findByStatus("1");
        //数据转换-{name:steven}-先把List转换成json串，再把json串转成List
        List<SkuInfo> skuInfos = JSON.parseArray(JSON.toJSONString(result.getData()), SkuInfo.class);
        //把规格转成Map
        for (SkuInfo info : skuInfos) {
            Map specMap = JSON.parseObject(info.getSpec(), Map.class);
            info.setSpecMap(specMap);
        }
        //导入到索引库中
        skuEsMapper.saveAll(skuInfos);
    }

    @Autowired
    private ElasticsearchTemplate esTemplate;
    @Override
    public Map search(Map<String, String> searchMap) {
        Map map = new HashMap();
        //1、构建查询条件
        NativeSearchQueryBuilder builder = builderBasicQuery(searchMap);
        //2、根据查询条件-查询商品列表
        searchList(map, builder);

        //代码优化前的逻辑
        /*//3、根据查询条件-分组查询商品分类列表
        searchCategoryList(map, builder);
        //4、根据查询条件-分组查询品牌列表
        searchBrandList(map, builder);
        //5、根据查询条件-分组查询规格列表
        searchSpec(map, builder);*/

        //代码优化后的逻辑
        //根据查询条件-分组查询分类、品牌与规格列表
        searchGroup(map,builder);
        return map;
    }

    /**
     * 根据查询条件-分组查询分类、品牌与规格列表
     * @param map 结果集包装
     * @param builder 查询条件
     */
    private void searchGroup(Map map,NativeSearchQueryBuilder builder){
        //1.设置分组域名-termsAggregationBuilder = AggregationBuilders.terms(别名).field(域名); size(查询的记录数)
        //TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("group_spec").field("spec.keyword").size(1000000);
        //2.添加分组查询参数-builder.addAggregation(termsAggregationBuilder)
        //分类聚合查询
        builder.addAggregation(AggregationBuilders.terms("group_category").field("categoryName"));
        //品牌聚合查询
        builder.addAggregation(AggregationBuilders.terms("group_brand").field("brandName"));
        //规格聚合查询
        builder.addAggregation(AggregationBuilders.terms("group_spec").field("spec.keyword").size(1000000));
        //3.执行搜索-esTemplate.queryForPage(builder.build(), SkuInfo.class)
        AggregatedPage<SkuInfo> page = esTemplate.queryForPage(builder.build(), SkuInfo.class);
        //4.获取所有分组查询结果集-page.getAggregations()
        Aggregations aggregations = page.getAggregations();
        //提取分类数据
        List<String> categoryList = getGroupResult(aggregations, "group_category");
        map.put("categoryList", categoryList);

        //提取品牌数据
        List<String> brandList = getGroupResult(aggregations, "group_brand");
        map.put("brandList", brandList);

        //提取规格数据
        //5.提取分组结果数据-stringTerms = aggregations.get(填入刚才查询时的别名)
        List<String> specList = getGroupResult(aggregations, "group_spec");
        //构建所有规格列表
        Map<String, Set> specMap = new HashMap<String, Set>();
        //遍历读取所有的specList
        for (String spec : specList) {
            //当前的spec对象-{"电视音响效果":"小影院","电视屏幕尺寸":"20英寸","尺码":"165"}
            Map<String,String> tempMap = JSON.parseObject(spec, Map.class);
            for (String key : tempMap.keySet()) {
                //读取原来的列表
                Set values = specMap.get(key);
                if(values == null){
                    values = new HashSet();
                }
                //向值列表追加元素
                values.add(tempMap.get(key));
                //把值列表重新放入，规格列表中
                specMap.put(key,values);
            }
        }
        //Mar<String,Set>
        //8.返回分类数据列表-map.put("categoryList", categoryList)
        map.put("specMap", specMap);
    }

    /**
     * 提取分组结果集
     * @param aggregations 查询到的集合结果集
     * @param group_name 域的别名
     * @return 提取的结果
     */
    private List<String> getGroupResult(Aggregations aggregations, String group_name) {
        //5.提取分组结果数据-stringTerms = aggregations.get(填入刚才查询时的别名)
        StringTerms stringTerms = aggregations.get(group_name);
        //6.定义分类名字列表-categoryList = new ArrayList<String>()
        List<String> specList = new ArrayList<String>();
        //7.遍历读取分组查询结果-stringTerms.getBuckets().for
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            //7.1获取分类名字，并将分类名字存入到集合中-bucket.getKeyAsString()
            specList.add(bucket.getKeyAsString());
        }
        return specList;
    }

    /**
     * 根据查询条件-分组查询规格列表
     * @param map 结果集包装
     * @param builder 查询条件
     */
    private void searchSpec(Map map,NativeSearchQueryBuilder builder){
        //1.设置分组域名-termsAggregationBuilder = AggregationBuilders.terms(别名).field(域名); size(查询的记录数)
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("group_spec").field("spec.keyword").size(1000000);
        //2.添加分组查询参数-builder.addAggregation(termsAggregationBuilder)
        builder.addAggregation(termsAggregationBuilder);
        //3.执行搜索-esTemplate.queryForPage(builder.build(), SkuInfo.class)
        AggregatedPage<SkuInfo> page = esTemplate.queryForPage(builder.build(), SkuInfo.class);
        //4.获取所有分组查询结果集-page.getAggregations()
        Aggregations aggregations = page.getAggregations();
        //5.提取分组结果数据-stringTerms = aggregations.get(填入刚才查询时的别名)
        List<String> specList = getGroupResult(aggregations, "group_spec");

        //构建所有规格列表
        Map<String, Set> specMap = new HashMap<String, Set>();
        //遍历读取所有的specList
        for (String spec : specList) {
            //当前的spec对象-{"电视音响效果":"小影院","电视屏幕尺寸":"20英寸","尺码":"165"}
            Map<String,String> tempMap = JSON.parseObject(spec, Map.class);
            for (String key : tempMap.keySet()) {
                //读取原来的列表
                Set values = specMap.get(key);
                if(values == null){
                    values = new HashSet();
                }
                //向值列表追加元素
                values.add(tempMap.get(key));
                //把值列表重新放入，规格列表中
                specMap.put(key,values);
            }
        }

        //Mar<String,Set>
        //8.返回分类数据列表-map.put("categoryList", categoryList)
        map.put("specMap", specMap);
    }

    /**
     * 根据查询条件-分组查询品牌列表
     * @param map 结果集包装
     * @param builder 查询条件
     */
    private void searchBrandList(Map map,NativeSearchQueryBuilder builder){
        //1.设置分组域名-termsAggregationBuilder = AggregationBuilders.terms(别名).field(域名);
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("group_brand").field("brandName");
        //2.添加分组查询参数-builder.addAggregation(termsAggregationBuilder)
        builder.addAggregation(termsAggregationBuilder);
        //3.执行搜索-esTemplate.queryForPage(builder.build(), SkuInfo.class)
        AggregatedPage<SkuInfo> page = esTemplate.queryForPage(builder.build(), SkuInfo.class);
        //4.获取所有分组查询结果集-page.getAggregations()
        Aggregations aggregations = page.getAggregations();
        //5.提取分组结果数据-stringTerms = aggregations.get(填入刚才查询时的别名)
        List<String> brandList = getGroupResult(aggregations, "group_brand");
        //8.返回分类数据列表-map.put("categoryList", categoryList)
        map.put("brandList", brandList);
    }

    /**
     * 根据查询条件-分组查询商品分类列表
     * @param map 结果集包装
     * @param builder 查询条件
     */
    private void searchCategoryList(Map map,NativeSearchQueryBuilder builder){
        //1.设置分组域名-termsAggregationBuilder = AggregationBuilders.terms(别名).field(域名);
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("group_category").field("categoryName");
        //2.添加分组查询参数-builder.addAggregation(termsAggregationBuilder)
        builder.addAggregation(termsAggregationBuilder);
        //3.执行搜索-esTemplate.queryForPage(builder.build(), SkuInfo.class)
        AggregatedPage<SkuInfo> page = esTemplate.queryForPage(builder.build(), SkuInfo.class);
        //4.获取所有分组查询结果集-page.getAggregations()
        Aggregations aggregations = page.getAggregations();
        //5.提取分组结果数据-stringTerms = aggregations.get(填入刚才查询时的别名)
        List<String> categoryList = getGroupResult(aggregations, "group_category");
        //8.返回分类数据列表-map.put("categoryList", categoryList)
        map.put("categoryList", categoryList);
    }

    /**
     * 根据查询条件-查询商品列表
     * @param map 结果集包装
     * @param builder 查询条件
     */
    private void searchList(Map map, NativeSearchQueryBuilder builder) {

        //h1.配置高亮查询信息-hField = new HighlightBuilder.Field()
        //h1.1:设置高亮域名-在构造函数中设置
        HighlightBuilder.Field hField = new HighlightBuilder.Field("name");
        //h1.2：设置高亮前缀-hField.preTags
        hField.preTags("<em style='color:red;'>");
        //h1.3：设置高亮后缀-hField.postTags
        hField.postTags("</em>");
        //h1.4：设置碎片大小-hField.fragmentSize
        hField.fragmentSize(100);
        //h1.5：追加高亮查询信息-builder.withHighlightFields()
        builder.withHighlightFields(hField);
        
        //3、获取NativeSearchQuery搜索条件对象-builder.build()
        NativeSearchQuery query = builder.build();
        //没有高亮的查询方式
        //4.查询数据-esTemplate.queryForPage(条件对象,搜索结果对象)
        //AggregatedPage<SkuInfo> page = esTemplate.queryForPage(query, SkuInfo.class);

        //使用高亮查询
        //h2.高亮数据读取-AggregatedPage<SkuInfo> page = esTemplate.queryForPage(query, SkuInfo.class, new SearchResultMapper(){})
        AggregatedPage<SkuInfo> page = esTemplate.queryForPage(query, SkuInfo.class, new SearchResultMapper() {
            @Override
            //h2.1实现mapResults(查询到的结果,数据列表的类型,分页选项)方法
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                //h2.2 先定义一组查询结果列表-List<T> list = new ArrayList<T>()
                List<T> list = new ArrayList<T>();
                //h2.3 遍历查询到的所有高亮数据-response.getHits().for
                for (SearchHit hit : response.getHits()) {
                    //h2.3.1 先获取当次结果的原始数据(无高亮)-hit.getSourceAsString()
                    String json = hit.getSourceAsString();
                    //h2.3.2 把json串转换为SkuInfo对象-skuInfo = JSON.parseObject()
                    SkuInfo skuInfo = JSON.parseObject(json, SkuInfo.class);
                    //h2.3.3 获取name域的高亮数据-nameHighlight = hit.getHighlightFields().get("name")
                    HighlightField nameHighlight = hit.getHighlightFields().get("name");
                    //h2.3.4 如果高亮数据不为空-读取高亮数据
                    if(nameHighlight != null){
                        //h2.3.4.1 定义一个StringBuffer用于存储高亮碎片-buffer = new StringBuffer()
                        StringBuffer buffer = new StringBuffer();
                        //h2.3.4.2 循环组装高亮碎片数据- nameHighlight.getFragments().for(追加数据)
                        for (Text fragment : nameHighlight.getFragments()) {
                            buffer.append(fragment);
                        }
                        //h2.3.4.3 将非高亮数据替换成高亮数据-skuInfo.setName()
                        skuInfo.setName(buffer.toString());
                    }
                    //h2.3.5 将替换了高亮数据的对象封装到List中-list.add((T) esItem)
                    list.add((T) skuInfo);
                }
                //h2.4 返回当前方法所需要参数-new AggregatedPageImpl<T>(数据列表，分页选项,总记录数)
                //h2.4 参考new AggregatedPageImpl<T>(list,pageable,response.getHits().getTotalHits())
                return new AggregatedPageImpl<T>(list,pageable,response.getHits().getTotalHits());
            }
        });

        //5、包装结果并返回
        map.put("rows", page.getContent());
        map.put("total", page.getTotalElements());
        map.put("totalPages", page.getTotalPages());
        int pageNum = query.getPageable().getPageNumber();  //当前页
        map.put("pageNum", pageNum);
        int pageSize = query.getPageable().getPageSize();//每页查询的条数
        map.put("pageSize", pageSize);
    }

    /**
     * 构建查询条件
     * @param searchMap 用户传入的条件
     * @return
     */
    private NativeSearchQueryBuilder builderBasicQuery(Map<String, String> searchMap) {
        //1、创建查询条件构建器-builder = new NativeSearchQueryBuilder()
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        //2、组装查询条件
        if(searchMap != null){
            //多条件匹配构建器
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            //2.1关键字搜索-builder.withQuery(QueryBuilders.matchQuery(域名，内容))
            String keywords = searchMap.get("keywords") == null ? "" : searchMap.get("keywords");
            //用户传入的关键字
            if(StringUtils.isNotEmpty(keywords)){
                //builder.withQuery(QueryBuilders.matchQuery("name", keywords));
                //分词匹配搜索
                boolQueryBuilder.must(QueryBuilders.matchQuery("name", keywords));
            }
            //2.2分类搜索-builder.withQuery(QueryBuilders.matchQuery(域名，内容))
            String category = searchMap.get("category") == null ? "" : searchMap.get("category");
            //用户传入的分类
            if(StringUtils.isNotEmpty(category)){
                boolQueryBuilder.must(QueryBuilders.termQuery("categoryName", category));
            }
            //2.3品牌搜索-builder.withQuery(QueryBuilders.matchQuery(域名，内容))
            String brand = searchMap.get("brand") == null ? "" : searchMap.get("brand");
            //用户传入的品牌
            if(StringUtils.isNotEmpty(brand)){
                boolQueryBuilder.must(QueryBuilders.termQuery("brandName", brand));
            }

            //2.4规格搜索
            for (String key : searchMap.keySet()) {
                // 如果是规格-spec_网络
                if(key.startsWith("spec_")){
                    //拼接查询的域名-"specMap." + 网络 + .keyword
                    String specField = "specMap." +key.substring(5) + ".keyword";
                    boolQueryBuilder.must(QueryBuilders.termQuery(specField, searchMap.get(key)));
                }
            }

            //2.5价格区间搜索-builder.withQuery(QueryBuilders.matchQuery(域名，内容))
            //0-500 500-1000 3000
            String price = searchMap.get("price") == null ? "" : searchMap.get("price");
            //用户传入的品牌
            if(StringUtils.isNotEmpty(price)){
                //[0,500] [3000]
                String[] split = price.split("-");
                //范围匹配构建器
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
                //价格区间: 开始价格 <= price <= 结束价格
                //如果传的不是3000
                if(split.length > 1){
                    //price <= 500
                    rangeQueryBuilder.lte(split[1]);
                }
                // price >= 0
                rangeQueryBuilder.gte(split[0]);
                //追加范围匹配过滤
                boolQueryBuilder.must(rangeQueryBuilder);
            }
            //添加多域匹配条件
            builder.withQuery(boolQueryBuilder);

            //2.6分页查询
            //当前页：默认第一页
            Integer pageNum = searchMap.get("pageNum") == null ? 0 : new Integer(searchMap.get("pageNum"));
            //每页查询的记录数
            Integer pageSize = 5;
            //注意：PageRequest.of(当前页[从0开始],查询的条数)
            PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
            //设置分页参数
            builder.withPageable(pageRequest);

            //2.7排序查询
            //排序的方式：ASC|DESC
            String sortRule = searchMap.get("sortRule") == null ? "" : searchMap.get("sortRule");
            //排序的域名
            String sortField = searchMap.get("sortField") == null ? "" : searchMap.get("sortField");
            //"   "  (isNotBlank  == fasle)  (isNotEmpty == true)
            //如果传入了排序参数
            if (StringUtils.isNotEmpty(sortField)) {
                //builder.withSort()-设置排序条件
                //SortBuilders.fieldSort-设置排序的域
                //order()-设置排序方式：ASC|DESC
                builder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.valueOf(sortRule)));
            }
        }
        return builder;
    }
}
