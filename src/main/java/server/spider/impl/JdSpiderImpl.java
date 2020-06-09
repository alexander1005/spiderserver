package server.spider.impl;

import org.assertj.core.util.Lists;
import org.bouncycastle.math.raw.Mod;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import server.config.Config;
import server.response.Response;
import server.response.Result;
import server.spider.Spider;
import server.util.SpringUtil;
import us.codecraft.xsoup.XElements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class JdSpiderImpl extends Spider {

  private String searchKey;

  public JdSpiderImpl(String name) {
    super("Smzdm:" + name);
    name = name.replace(" ","%20");
    this.searchKey = name;
  }

  @Override
  public void onStart(Config config) {
    String cookie = "__jdc=122270672; mba_muid=1663423016; __jd_ref_cls=Mnpm_ComponentApplied; __jda=122270672.1663423016.1578533487.1578998861.1591682752.3; __jdv=122270672|direct|-|none|-|1591682752257; __jdu=1663423016; shshshfp=78aaa058b9f899df02e1532ec1c6be3b; shshshfpa=f10305ca-3007-1984-95ed-067262e42c20-1591682752; shshshfpb=mblE9qA1YjndSqKBsE4dIAA%3D%3D; areaId=19; ipLoc-djd=19-1601-3633-0; rkv=V0800; 3AB9D23F7A4B3C9B=JAVEYHQINNEKMAGTOAGFPRADCOYP2357L7ILN4UZMODO6QPNEENBPQEPMUBSJN7KKQ7NWIO77UEGSLPH2UJWZR2ESQ; qrsc=3; __jdb=122270672.5.1663423016|3.1591682752; shshshsID=45022ef4cd66557826afa15ebf3cb25f_5_1591682814182";
    this.config = config;
    String url = "https://search.jd.com/Search?keyword={1}&qrst=1&wq={1}&shop=1&click=1&page={2}&s=80";
    this.startUrls(
        url.replace("{1}", searchKey).replace("{2}", "1"),
        url.replace("{1}", searchKey).replace("{2}", "2"),
        url.replace("{1}", searchKey).replace("{2}", "3")
    );
    this.config.getHeaders().put("Content-Type", "text/html; charset=UTF-8");
    this.config.getHeaders().put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
    this.setCookie(cookie);
    this.addPipeline((item, request) -> {
      RabbitTemplate rabbitTemplate = SpringUtil.getBean(RabbitTemplate.class);
      rabbitTemplate.convertAndSend("topicExchange", "jd", item);
    });
  }

  @Override
  protected <T> Result<T> parse(Response response) {
    String s = response.body().toString();
    String HTTPS_PROTOCOL = "https:"; List<Model> modelList = Lists.newArrayList();
    Elements select = response.body().css("ul[class=gl-warp clearfix]").select("li[class=gl-item]");
    for (Element element : select){
      String url = HTTPS_PROTOCOL + element.select("a").attr("href");
      String goodsId = element.attr("data-sku");
      String goodsName = element.select("div[class=p-name p-name-type-2]").select("em").text();
      String goodsPrice = element.select("div[class=p-price]").select("strong").select("i").text();
      String imgUrl = HTTPS_PROTOCOL + element.select("div[class=p-img]").select("a").select("img").attr("src");
      String detail = element.select("i").get(1).text();
      Model model  = new Model();
      model.setTime(new Date());
      model.setDetail(detail);
      model.setSourceId(goodsId);
      model.setCreateTime(new Date());
      model.setSource("jd");
      model.setTitle(goodsName);
      model.setName(goodsName);
      model.setPrice(goodsPrice);
      model.setImage(imgUrl);
      model.setUrl(url);
      modelList.add(model);
      System.out.println(".........");
    }
    return new Result(modelList);
  }


}
