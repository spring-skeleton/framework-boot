package test.codeages.framework.biz;

import java.util.List;

public interface RoleService {
    Role save(Role role);
    public List<Role> findAll();
}
