package test.codeages.framework.jsonrpc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestComponent;

import java.util.List;

@TestComponent
@Slf4j
public class Rpc3Service {
    public String getUserNameByName(String name) {
        log.info("name {}", name);
        return name;
    }

    public List<String> getUserNames(List<String> list){
        return list;
    }

    public RpcVo getRpcVo(RpcVo vo){
        return vo;
    }
}
