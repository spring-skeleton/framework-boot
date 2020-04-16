package test.codeages.framework.event;

import com.codeages.framework.event.BaseListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.boot.test.context.TestComponent;

@Slf4j
@TestComponent
public class CourseListener extends BaseListener {
    public void onApplicationEvent(CourseDeleteEvent event) {
        Assert.assertEquals("deleteCourse", event.getSource());
    }
}
