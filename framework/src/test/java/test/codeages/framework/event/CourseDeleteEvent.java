package test.codeages.framework.event;

import com.codeages.framework.event.BaseEvent;

public class CourseDeleteEvent extends BaseEvent<String> {
    public CourseDeleteEvent(String source) {
        super(source);
    }
}
