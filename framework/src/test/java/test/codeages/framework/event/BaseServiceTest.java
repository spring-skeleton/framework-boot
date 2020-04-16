package test.codeages.framework.event;

import test.codeages.framework.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseServiceTest extends BaseTest {

    @Autowired
    private CourseService courseService;

    @Test
    public void testPublishEvent(){
        courseService.deleteCourse();
    }
}
