package com.tms.threed.threedFramework.repo.server.rt;

import junit.framework.TestCase;

public class CommitTest extends TestCase {

    public void test1() throws Exception {
        String serialCommit1 = System.currentTimeMillis() + "|v1.2|4e5c18b2b9651c0af81140d66e0173fb3fc6b722|qq4e5c18b2b9651c0af81140d66e0173fb3fc6b7";
        Commit commit1 = new Commit(serialCommit1);

        String serialCommit2 = commit1.serialize();
        assertEquals(serialCommit1, serialCommit2);


        Commit commit2 = new Commit(serialCommit2);
        assertEquals(commit1, commit2);

        /*


        expected:<1299000670353|v1.2|4e5c18b2b9651c0af81140d66e0173fb3fc6b722|qq4e5c18b2b9651c0af81140d66e0173fb3fc6b7>
        but was: <1299000670353|v1.2|4e5c18b2b9651c0af81140d66e0173fb3fc6b722|qq4e5c18b2b9651c0af81140d66e0173fb3fc6b7>
         */

    }
}
