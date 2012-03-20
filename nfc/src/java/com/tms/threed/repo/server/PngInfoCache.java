package com.tms.threed.repo.server;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;

import java.util.Stack;

public interface PngInfoCache {
    PngInfo getPngInfo(Stack<String> stack, Repository repo, ObjectId id);
}
