package com.chlorine.spider.processor;

import com.chlorine.base.util.FileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebScraper {
    public static void main(String[] args) throws IOException {
        List<Map<String, String>> process = process(new File("/Users/chenlong/mine/chlorine/chlorine-modules/chlorine-spider/src/main/resources/test.txt"));
        System.out.println();

    }

    public static List<Map<String, String>> process(MultipartFile multipartFile) throws IOException {
        // 假设这是你需要爬取的 HTML 内容
        Document doc = Jsoup.parse(FileUtil.readFile(multipartFile));
        return process(doc);
    }
    public static List<Map<String, String>> process(String html) throws IOException {
        // 假设这是你需要爬取的 HTML 内容
        Document doc = Jsoup.parse(html);
        return process(doc);
    }

    public static List<Map<String, String>> process(File file) throws IOException {
        // 假设这是你需要爬取的 HTML 内容
        Document doc = Jsoup.parse(new File("/Users/chenlong/mine/chlorine/chlorine-modules/chlorine-spider/src/main/resources/test.txt"), "UTF-8");
        return process(doc);
    }

    public static List<Map<String, String>> process(Document doc) {
        // 假设这是你需要爬取的 HTML 内容

        // 获取所有包裹信息
        Elements packageElements = doc.select("div.lw-wrapinfo");

        // 获取所有商品信息
        Elements goodsRows = doc.select("div.lw-buyoff .table-tbody .table-tr");

        // 确保包裹和商品的数量是匹配的
        if (packageElements.size() != goodsRows.size()) {
            System.out.println("包裹信息和商品信息数量不匹配！");
            return new ArrayList<>();
        }

        // 遍历每个包裹和商品，按顺序输出对应的信息
        List<Map<String,String>> goodsList = new ArrayList<>();
        for (int i = 0; i < packageElements.size(); i++) {
            Element packageElement = packageElements.get(i);
            Element goodsRow = goodsRows.get(i);

            // 提取包裹信息
            String trackingNumber = packageElement.select("span:contains(转运单号)").text().split(":")[1].trim();
            String source = packageElement.select("span:contains(包裹来源)").text().split(":")[1].trim();
            String weight = packageElement.select("span.jsWeight").text().split(":")[1].trim();

            // 提取商品信息
            String brandNameCN = goodsRow.select("input[name^=goods_type_cn]").val();
            String brandNameEN = goodsRow.select("input[name^=goods_type_en]").val();
            String productNameCN = goodsRow.select("input[name^=goods_text_cn]").val();
            String productNameJP = goodsRow.select("input[name^=goods_text_jp]").val();
            String productSpec = goodsRow.select("input[name^=goods_text_detail]").val();
            String price = goodsRow.select("input[name^=goods_price]").val();
            String quantity = goodsRow.select("input[name^=goods_quantity]").val();
            String productLink = goodsRow.select("input[name^=goods_link]").val();

            Map<String,String> item = new HashMap<>();
            item.put("trackingNumber", trackingNumber);
            item.put("source", source);
            item.put("weight", weight);
            item.put("brandNameCN", brandNameCN);
            item.put("brandNameEN", brandNameEN);
            item.put("productNameCN", productNameCN);
            item.put("productNameJP", productNameJP);
            item.put("productSpec", productSpec);
            item.put("price", price);
            item.put("quantity", quantity);
            item.put("productLink", productLink);
            goodsList.add(item);

            // 输出包裹和商品对应信息
            System.out.println("包裹信息:");
            System.out.println("  转运单号: " + trackingNumber);
            System.out.println("  包裹来源: " + source);
            System.out.println("  重量: " + weight);

            System.out.println("商品信息:");
            System.out.println("  商品中文品牌名: " + brandNameCN);
            System.out.println("  商品英文/日文品牌名: " + brandNameEN);
            System.out.println("  商品中文名称: " + productNameCN);
            System.out.println("  商品日文名称: " + productNameJP);
            System.out.println("  商品规格: " + productSpec);
            System.out.println("  单价(日元): " + price);
            System.out.println("  数量: " + quantity);
            System.out.println("  商品链接: " + productLink);
            System.out.println("-----------------------------------------");

        }
        return goodsList;
    }
}
