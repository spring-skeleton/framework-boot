package test.codeages.framework.cache;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import test.codeages.framework.BaseTest;
import test.codeages.framework.biz.Org;

@Slf4j
public class CacheTest extends BaseTest {

    @Autowired
    private CachedOrgRepository orgRepository;

    @Autowired
    private CachedOrgService orgService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    public void testAddOrg(){
        Org savedOrg = saveMockOrg();
        Org org1 = orgService.getById(savedOrg.getId());
        Org org2 = orgService.getByNameAndCode(savedOrg.getName(), savedOrg.getCode());
        Org org3 = orgService.getByCodeAndName(savedOrg.getCode(), savedOrg.getName());
        Org org4 = orgService.getById(savedOrg.getId());

        assertOrg(savedOrg, org1);
        assertOrg(savedOrg, org2);
        assertOrg(savedOrg, org3);
        assertOrg(savedOrg, org4);

        savedOrg.setName("项目部");
        Org savedOrg2 = orgService.save(savedOrg);
        Org org5 = orgService.getById(savedOrg.getId());

        assertOrg(savedOrg, savedOrg2);
        assertOrg(savedOrg, org5);

        orgService.updateNameById(org5.getId(), "销售部");

    }

    protected void assertOrg(Org savedOrg, Org org1) {
        Assert.assertEquals(savedOrg.getName(),org1.getName());
        Assert.assertEquals(savedOrg.getCode(),org1.getCode());
        Assert.assertEquals(savedOrg.getId(),org1.getId());
        Assert.assertEquals(savedOrg.getCreatedTime(),org1.getCreatedTime());
    }

    protected Org mockOrg() {
        Org org = new Org();
        org.setName("技术部");
        org.setCode("rdc");
        return org;
    }

    protected Org saveMockOrg(){
        Org org = mockOrg();
        return orgRepository.save(org);
    }
}
