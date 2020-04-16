package test.codeages.framework.biz;

import com.codeages.framework.biz.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class Role extends BaseEntity {
    private String name;
    private String code;
}
