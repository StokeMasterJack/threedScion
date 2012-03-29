package smartClient.client;

import smartsoft.util.gwt.client.events3.Topic;
import smartsoft.util.gwt.client.events3.TopicSettings;

public class ThreedSessionTopic extends Topic<ThreedSession, ThreedSessionEvent, ThreedSessionListener> {

    public ThreedSessionTopic(ThreedSession publisher, TopicSettings settings) {
        super(publisher, settings);
    }

    public ThreedSessionTopic(ThreedSession publisher) {
        super(publisher);
    }

    public void fire() {
        fire(new ThreedSessionEvent(publisher));
    }

}
