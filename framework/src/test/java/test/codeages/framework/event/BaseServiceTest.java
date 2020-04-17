package test.codeages.framework.event;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.codeages.framework.BaseTest;

public class BaseServiceTest extends BaseTest {

    @Autowired
    private CourseService courseService;

    @Test
    public void testPublishEvent(){
        courseService.deleteCourse();
    }
}
