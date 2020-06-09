package server.pipeline;

import server.request.Request;

public interface Pipeline<T> {

  void process(T item, Request request);

}

