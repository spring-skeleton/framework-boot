package test.codeages.framework.event;

import com.codeages.framework.biz.BaseService;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class CourseService extends BaseService {
    public void deleteCourse(){
        this.publish(new CourseDeleteEvent("deleteCourse"));
    }
}
