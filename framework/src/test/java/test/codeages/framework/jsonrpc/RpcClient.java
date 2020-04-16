package test.codeages.framework.jsonrpc;

import com.codeages.framework.jsonrpc.JsonRpcClient;

import java.util.List;

@JsonRpcClient(server = "test.rpc", name = "RpcTest1Service")
interface Rpc1Client {
    public List<String> getUserNames(List<String> list);

    public String getUserNameByName(String name);

    public RpcVo getRpcVo(RpcVo vo);

    public String getMethodNotExist();

    public String getMethodWithException();
}

@JsonRpcClient(server = "test.rpc", name = "xxx")
interface Rpc2Client {
    public List<String> getUserNames(List<String> list);

    public String getUserNameByName(String name);

    public RpcVo getRpcVo(RpcVo vo);

    public String getMethodNotExist();

    public String getMethodWithException();
}