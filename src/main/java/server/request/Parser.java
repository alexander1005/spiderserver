package server.request;

import server.response.Response;
import server.response.Result;

/**
 * 解析器接口
 *
 * @author biezhi
 * @date 2018/1/12
 */
public interface Parser<T> {

    Result<T> parse(Response response);

}
