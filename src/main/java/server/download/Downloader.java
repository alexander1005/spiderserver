package server.download;

import server.request.Request;
import server.response.Response;
import server.scheduler.Scheduler;

import java.io.InputStream;

public class Downloader implements Runnable {

  private final Scheduler scheduler;
  private final Request request;

  public Downloader(Scheduler scheduler, Request request) {
    this.scheduler = scheduler;
    this.request = request;
  }

  @Override
  public void run() {
    io.github.biezhi.request.Request httpReq = null;
    if ("get".equalsIgnoreCase(request.method())) {
      httpReq = io.github.biezhi.request.Request.get(request.getUrl());
    }
    if ("post".equalsIgnoreCase(request.method())) {
      httpReq = io.github.biezhi.request.Request.post(request.getUrl());
    }

    InputStream result = httpReq.contentType(request.contentType()).headers(request.getHeaders())
        .connectTimeout(request.getSpider().getConfig().timeout())
        .readTimeout(request.getSpider().getConfig().timeout())
        .stream();

    Response response = new Response(request, result);
    scheduler.addResponse(response);
  }
}
