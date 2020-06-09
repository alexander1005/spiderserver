package server.response;

import lombok.Getter;
import server.request.Request;

import java.io.InputStream;

/**
 * 响应对象
 *
 * @date 2018/1/11
 */
public class Response {

    @Getter
    private Request request;
    private Body    body;

    public Response(Request request, InputStream inputStream) {
        this.request = request;
        this.body = new Body(inputStream, request.charset());
    }

    public Body body() {
        return body;
    }

}
