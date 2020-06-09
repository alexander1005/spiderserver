package server.spider.impl;
import	java.text.SimpleDateFormat;

import org.assertj.core.util.Lists;
import org.jsoup.select.Elements;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import server.config.Config;
import server.response.Response;
import server.response.Result;
import server.spider.Spider;
import server.util.SpringUtil;
import us.codecraft.xsoup.XElements;
import org.jsoup.nodes.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SmzdmSpiderImpl extends Spider {

  private String searchKey;

  public SmzdmSpiderImpl(String name) {
    super("Smzdm:" + name);
    this.searchKey = name;
  }

  @Override
  public void onStart(Config config) {
    String cookie = "__ckguid=SqY4gB6ea9YWNlycvQnnt84; FROM_BD=1; device_id=19001640201591602435346237a8bebe6db9514014ca32b65bdc0c2aab; _gid=GA1.2.1685439107.1591602435; _ga=GA1.2.20980401.1591602435; __jsluid_s=db5b97a8247cbbc33920c8e839c33ea6; m_ss_ab=ss98; homepage_sug=i; r_sort_type=score; __gads=ID=584e47c7950ae6c2:T=1591603706:S=ALNI_MbO-IIpqQAe6Muvvkac5dsKkfEXNQ; s_his=IPHONE11; deeplink_url=smzdm%3A%2F%2Fyuanchuang%2Faqn9r30v%3Fjson%3D%7B%22channelName%22%3A%22yuanchuang%22%2C%22linkVal%22%3A%22aqn9r30v%22%2C%22keyWord%22%3A%22%22%7D; _zdmA.uid=ZDMA.JmwezZzBS.1591665300.2419200; Hm_lvt_9b7ac3d38f30fe89ff0b8a0546904e58=1591602434,1591603705,1591665301; Hm_lpvt_9b7ac3d38f30fe89ff0b8a0546904e58=1591665301; zdm_qd=%7B%22utm_source%22%3A%22baidu%22%2C%22utm_medium%22%3A%22cpc%22%2C%22utm_campaign%22%3A%220011%22%2C%22referrer%22%3A%22https%3A%2F%2Fwww.baidu.com%2Fbaidu.php%3Fsc.000000j0xnZgmfstWUDGx93mrdT8GTmDYXLeZb4Bco-XBRW4MjjfkTU5Bp7u1FhXfc1qufvG9wKBEsUxIN6vNgSOnq8i-S-ZRRhtjK9GL2PJJddW1FGM4HyHegjz9Ndj8ZTl8R77Z9yZC3mbGTtOF7Xc7BV9C5YEcdrxKP-H8rQAQrYpe35wsSoMFLXPrHREL-9v-UeldfH8eWnsQWAldFvAuLtK.7D_NR2Ar5Od663rj6tCq5jIGwKLsRP5QAeKPa-BqM76l32AM-YG8x6Y_f33X8a9G4myIrP-SJFBzz8Vi_nYQZZkLU-0.U1Yk0ZDqzoQjVPpdVT5at6Kspynqn0KsTv-MUWYkn1-hPv7hnj04Pvw9m19WPHf3P1nLnHfkuj-bnynvmsKY5IpdVT5at6KGUHYznWR0u1dsmvqsXfKdpHdBmy-bIfKspyfqP0KWpyfqrjf0UgfqnH0kPdtknjD4g1csPWFxnH0zndt1PW0k0AVG5H00TMfqPjcs0ANGujY1nHbsPNtkPjnzg1nkPWTzg1cknH0Yg1nknjm4g1nknWTsg1nsrjcYg1cvn1Rsg1cznHR30AFG5HDdPNtkPH9xnW0Yg1ckPsKVm1YkrjnzP1D1rjf4g1Dsnj7xnHDsPjcYP10vn1NxnNts0Z7spyfqn0Kkmv-b5H00ThIYmyTqn0K9mWYsg100ugFM5Hc0TZ0qn0K8IM0qna3snj0snj0sn0KVIZ0qn0KbuAqs5HD0ThCqn0KbugmqTAn0uMfqn0KspjYs0Aq15H00mMTqnH00UMfqn0K1XWY0mgPxpywW5gK1QyIlUMn0pywW5R9rf6KYIgnqPjT4Pjn1rjbLn1bzrjmzPjckPsKzug7Y5HDdrHDvPWRzrHbdnjm0Tv-b5HbLrHcYnjTYnj0suj03uH00mLPV5RFKfWN7PDm4wjIAPb77nHD0mynqnfKsUWYs0Z7VIjYs0Z7VT1Ys0ZGY5H00UyPxuMFEUHYsg1Kxn7tsg100uA78IyF-gLK_my4GuZnqn7tsg1Kxn7ts0ZK9I7qhUA7M5H00uAPGujYs0ANYpyfqQHD0mgPsmvnqn0KdTA-8mvnqn0KkUymqn0KhmLNY5H00pgPWUjYs0ZGsUZN15H00mywhUA7M5HD0UAuW5H00uAPWujY0mhwGujY1wj6vrj0kf1cYnjKKwWwDn1c1PR7KnH6Yf1n3wHmznfKBIjYs0Aq9IZTqn0KEIjYs0AqzTZfqnanscznsc10WnansQW0snj0snanscznsczYWna3snj0snj0Wni3snj0snj00TNqv5H08rH9xna3sn7tsQW0sg108PWKxna3drNtsQWnv0AN3IjYs0AF1gLKzUvwGujYs0APzm1YYnWR3P0%26ck%3D8013.3.106.318.179.442.197.364%26shh%3Dwww.baidu.com%26sht%3Dbaidu%26us%3D1.0.1.0.1.301.0%26ie%3Dutf-8%26f%3D8%26tn%3Dbaidu%26wd%3D%25E4%25BB%2580%25E4%25B9%2588%25E5%2580%25BC%25E5%25BE%2597%25E4%25B9%25B0%26fenlei%3D256%26rqlang%3Dcn%26inputT%3D6024%26bc%3D110101%22%7D";
    this.config = config;
    this.config.getHeaders().put("Accept-Language","zh-CN,zh;q=0.9,en;q=0.8");
    this.config.getCookies().put("_zdmA.uid","ZDMA.JmwezZzBS.1591603704.2419200");
    this.config.getCookies().put("__ckguid","SqY4gB6ea9YWNlycvQnnt84");
    this.startUrls(
        "https://search.m.smzdm.com/?s=" + searchKey + "&source=hot_keyword&v=b&p=1&order=time",
        "https://search.m.smzdm.com/?s=" + searchKey + "&source=hot_keyword&v=b&p=2&order=time",
        "https://search.m.smzdm.com/?s=" + searchKey + "&source=hot_keyword&v=b&p=3&order=time",
        "https://search.m.smzdm.com/?s=" + searchKey + "&source=hot_keyword&v=b&p=4&order=time",
        "https://search.m.smzdm.com/?s=" + searchKey + "&source=hot_keyword&v=b&p=5&order=time"
    );
    this.config.getHeaders().put("Content-Type","text/html; charset=UTF-8");
    this.config.getHeaders().put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
    this.setCookie(cookie);
    this.addPipeline((item, request) -> {
      RabbitTemplate rabbitTemplate = SpringUtil.getBean(RabbitTemplate.class);
      rabbitTemplate.convertAndSend("topicExchange","smzdm",item);
      System.out.println(item.toString());
    });
  }

  @Override
  protected <T> Result<T> parse(Response response) {
    String s = response.body().toString();
    System.out.println(s);
    XElements xpath = response.body().xpath("/html/body/div[2]/ul/li");
    Elements elements = xpath.getElements();
    String start = "http:";
    List<Model> modelList = Lists.newArrayList();

    for (Element element : elements) {
      Model model = new Model();
      Elements select = element.select("a[href]");
      String url = start + select.attr("href");
      String title = element.select("a[href]").select(".zm-card-title").text();
      String price = element.select("a[href]").select(".card-price").text();
      String mall = element.select("a[href]").select(".card-mall").text();
      String time = element.select("a[href]").select(".zm-card-actions-left span span").get(1).text();
      String img = start + select.select(".zm-card-media img").attr("src");
      String substring = url.substring(0, url.length() - 1);
      int i = substring.lastIndexOf('/');
      String id = substring.substring(i + 1, substring.length());
      model.setSourceId(id);
      model.setCreateTime(new Date());
      model.setImage(img);
      model.setModel(searchKey);
      model.setName(title);
      model.setTitle(searchKey);
      String t ;
      if (time.contains(":")){
        t = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) ;
        t += " ";
        t += time;
      }else{
        String yyyy = new SimpleDateFormat("yyyy").format(new Date());
        t = yyyy +"-"+time.trim()+ " 00:00";
      }
      try {
        Date parse = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(t);
        model.setTime(parse);
      } catch (ParseException e) {
        e.printStackTrace();
      }
      model.setPrice(price);
      model.setUrl(url);
      model.setSource(mall);
      model.setTitle(title);
      String replace = title.replace(" ", "");
      if (replace.toLowerCase().contains(searchKey.toLowerCase())) {
        modelList.add(model);
      }
    }
//    modelList = modelList.stream().filter(item->item.getPrice().length()<12).collect(Collectors.toList());
    return new Result(modelList);
  }


}
