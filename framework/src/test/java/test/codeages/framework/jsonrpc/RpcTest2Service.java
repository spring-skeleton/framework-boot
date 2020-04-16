package test.codeages.framework.jsonrpc;

import com.codeages.framework.jsonrpc.JsonRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import java.util.List;

@JsonRpcService("xxx")
@TestComponent
public class RpcTest2Service {

    @Autowired
    private Rpc3Service rpc3Service;

    public List<String> getUserNames(List<String> list){
        return rpc3Service.getUserNames(list);
    }

    public String getUserNameByName(String name) {
        return rpc3Service.getUserNameByName(name);
    }

    public RpcVo getRpcVo(RpcVo vo){
        return rpc3Service.getRpcVo(vo);
    }

    public String getMethodWithException(){
        throw new RuntimeException("Something wrong");
    }
}
