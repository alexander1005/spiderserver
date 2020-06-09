package server.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import server.request.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * 响应结果封装
 * <p>
 * 存储 Item 数据和新添加的 Request 列表
 *
 * @date 2018/1/12
 */
@Data
@NoArgsConstructor
public class Result<T> {

    private List<Request> requests = new ArrayList<>();
    private T item;

    public Result(T item) {
        this.item = item;
    }

    public Result addRequest(Request request) {
        this.requests.add(request);
        return this;
    }

    public Result addRequests(List<Request> requests) {
        if (!CollectionUtils.isEmpty(requests)) {
            this.requests.addAll(requests);
        }
        return this;
    }

}
