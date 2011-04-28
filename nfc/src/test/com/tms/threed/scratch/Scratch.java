package com.tms.threed.scratch;

import junit.framework.TestCase;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import java.io.File;

public class Scratch extends TestCase {

    public void test1() throws Exception {
        ObjectMapper m = new ObjectMapper();



        // can either use mapper.readTree(JsonParser), or bind to JsonNode
        JsonNode rootNode = m.readValue(new File("user.json"), JsonNode.class);


        // ensure that "last name" isn't "Xmler"; if is, change to "Jsoner"
        JsonNode nameNode = rootNode.path("name");
        String lastName = nameNode.path("last").getTextValue();
        if ("xmler".equalsIgnoreCase(lastName)) {
            ((ObjectNode) nameNode).put("last", "Jsoner");
        }
// and write it out:
        m.writeValue(new File("user-modified.json"), rootNode);
    }
}
