package com.tms.threed.threedFramework.servletUtil;

import junit.framework.TestCase;

public class ExceptionRendererTest extends TestCase {

    public void test() throws Exception {

        try {
            dangerCodeL1();
        } catch (Exception e) {
            System.out.println(ExceptionRenderer.render(e));
            System.out.println("=====");
            System.out.println(ExceptionRenderer.renderAsHtmlComment(e));
        }


    }

    public void dangerCodeL1() throws Exception {
        try {
            dangerCodeL2();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void dangerCodeL2() throws Exception {
        try {
            dangerCodeL3();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void dangerCodeL3() throws Exception {
        try {
            dangerCodeL4();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void dangerCodeL4() throws Exception {
        Integer.parseInt(">");
    }


}
