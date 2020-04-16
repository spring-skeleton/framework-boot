package test.codeages.framework.jsonrpc;

import lombok.Data;

import java.util.Date;

@Data
public class RpcVo {
    private Long id;
    private String name;
    private Date time;
}
