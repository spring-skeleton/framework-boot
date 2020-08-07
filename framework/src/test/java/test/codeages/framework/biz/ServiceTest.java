package test.codeages.framework.biz;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import test.codeages.framework.BaseTest;

import java.util.HashMap;
import java.util.Map;

public class ServiceTest extends BaseTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private OrgRepository orgRepository;

    @Test
    public void testSave() {
        Role role = mockRole();
        Role savedRole = roleService.save(role);
        Assert.assertEquals(role.getName(),savedRole.getName());
        Assert.assertEquals(role.getCode(),savedRole.getCode());

        Org org = mockOrg();
        Org savedOrg = orgService.save(org);
        Assert.assertEquals(org.getName(),savedOrg.getName());
        Assert.assertEquals(org.getCode(),savedOrg.getCode());

        Org one = orgRepository.findByName("技术部");
        Assert.assertEquals(org.getName(),one.getName());
    }

    @Test
    public void testSaveDto(){
        OrgDto orgDto = new OrgDto();
        orgDto.setCode("技术部");
        orgDto.setName("rdc");
        Org savedOrg = orgService.saveByDto(orgDto);
        Assert.assertEquals(orgDto.getName(),savedOrg.getName());
    }

    @Test
    public void testFindAll() {
        saveMockRole();
        Assert.assertEquals(1,roleService.findAll().size());
        saveMockRole();
        saveMockRole();
        Assert.assertEquals(3,roleService.findAll().size());

        saveMockOrg();
        Assert.assertEquals(1,orgService.findAll().size());
        saveMockOrg();
        saveMockOrg();
        Assert.assertEquals(3,orgService.findAll().size());
    }

    @Test
    public void testByConditions() {
        saveMockOrg();
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("name", "技术");
        Pageable pageable = PageRequest.of(1, 100);
        Assert.assertEquals(0, orgService.findByConditions(conditions, pageable).getTotalPages());
    }

    @Test
    public void testByEmptyConditions() {
        saveMockOrg();
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("name", "");
        Pageable pageable = PageRequest.of(1, 100);
        orgService.findByConditions(conditions, pageable);
        Assert.assertEquals(1, orgService.findByConditions(conditions, pageable).getTotalPages());
    }

    @Test
    public void testByLikeConditions() {
        saveMockOrg();
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("nameLike", "技术");
        Pageable pageable = PageRequest.of(1, 100);
        Assert.assertEquals(1, orgService.findByConditions(conditions, pageable).getTotalPages());
    }

    protected Role mockRole() {
        Role role = new Role();
        role.setName("管理员");
        role.setCode("admin");
        return role;
    }

    protected Role saveMockRole(){
        Role role = mockRole();
        return roleService.save(role);
    }

    protected Org mockOrg() {
        Org role = new Org();
        role.setName("技术部");
        role.setCode("rdc");
        return role;
    }

    protected Org saveMockOrg(){
        Org org = mockOrg();
        return orgService.save(org);
    }
}
