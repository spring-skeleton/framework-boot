package test.codeages.framework.biz;

import com.codeages.framework.biz.BaseEntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrgMapper extends BaseEntityMapper<OrgDto, Org> {
}
