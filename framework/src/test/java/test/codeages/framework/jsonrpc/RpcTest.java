package test.codeages.framework.jsonrpc;

import com.codeages.framework.jsonrpc.JsonRpcException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import test.codeages.framework.BaseTest;

public class RpcTest extends BaseTest {

    @Autowired
    private Rpc1Client rpc1Client;

    @Autowired
    private Rpc2Client rpc2Client;

    @Test(expected = JsonRpcException.class)
    public void testRpc() {
        Assert.assertEquals("张三", rpc1Client.getUserNameByName("张三"));
        Assert.assertEquals("张三", rpc2Client.getUserNameByName("张三"));

        List<String> names = new ArrayList<>();
        names.add("张三");
        Assert.assertEquals("张三", rpc1Client.getUserNames(names).get(0));
        Assert.assertEquals("张三", rpc2Client.getUserNames(names).get(0));

        RpcVo rpcVo = new RpcVo();
        rpcVo.setId(1L);
        rpcVo.setName("张三");
        rpcVo.setTime(new Date());
        Assert.assertEquals("张三", rpc1Client.getRpcVo(rpcVo).getName());
        Assert.assertEquals("张三", rpc2Client.getRpcVo(rpcVo).getName());
    }

    @Test(expected = JsonRpcException.class)
    public void testRpcWithMethodNotExist(){
        rpc1Client.getMethodNotExist();
        rpc2Client.getMethodNotExist();
    }

    @Test(expected = JsonRpcException.class)
    public void testRpcWithException() {
        rpc1Client.getMethodWithException();
        rpc2Client.getMethodWithException();
    }
}
