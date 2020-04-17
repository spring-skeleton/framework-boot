package test.codeages.framework.biz;

import com.codeages.framework.biz.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class Org extends BaseEntity {
    private String name;
    private String code;

    public boolean equals(Org obj) {
        return this.name.equals(obj.name)
                && this.code.equals(obj.code)
                && this.getId().equals(obj.getId())
                && this.getCreatedTime().equals(obj.getCreatedTime())
                && this.getUpdatedTime().equals(obj.getUpdatedTime());
    }

}

