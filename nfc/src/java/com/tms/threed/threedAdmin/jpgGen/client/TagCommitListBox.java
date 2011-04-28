package com.tms.threed.threedAdmin.jpgGen.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;
import com.tms.threed.threedAdmin.main.client.services.FetchTagsCallback;
import com.tms.threed.threedAdmin.main.client.services.ThreedAdminService1Async;
import com.tms.threed.threedFramework.repo.shared.TagCommit;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;
import com.tms.threed.threedFramework.util.gwtUtil.client.Console;

import java.util.ArrayList;
import java.util.List;

public class TagCommitListBox extends ListBox {

    private final ThreedAdminService1Async service;
    private final List<TagCommit> tagCommits = new ArrayList<TagCommit>();

    public TagCommitListBox(ThreedAdminService1Async service) {
        this.service = service;
        setWidth("6em");
    }

    @Override public void clear() {
        super.clear();
        tagCommits.clear();
    }

    public TagCommit getSelectedTagCommit() {
        int i = getSelectedIndex();
        if (i == -1) return null;
        return tagCommits.get(i);
    }

    public void selectFirst() {
        if (tagCommits.isEmpty()) return;
        setSelectedIndex(0);
    }

    private void add(TagCommit tagCommit) {
        tagCommits.add(tagCommit);
        addItem(tagCommit.getDisplayName());
    }

//    private static void sort(List<TagCommit> tags) {
//        Collections.sort(tags);
//
//    }

    private static void removeRedundantHead(List<TagCommit> tags) {
        TagCommit taggedAsHead = null;
        for (TagCommit tagCommit : tags) {
            if (tagCommit.isTaggedAsHead()) {
                taggedAsHead = tagCommit;
                break;
            }
        }
        if (taggedAsHead == null) return;

        boolean taggedAsHeadIsRedundant = false;
        for (TagCommit tc : tags) {
            if (!tc.isTaggedAsHead() && tc.getRootTreeId().equals(taggedAsHead.getRootTreeId())) {
                taggedAsHeadIsRedundant = true;
                break;
            }
        }

        if (taggedAsHeadIsRedundant) {
            tags.remove(taggedAsHead);
        }

    }


    public void fetchTags(final SeriesKey seriesKey) {

        service.service2.getTagCommits(seriesKey, new AsyncCallback<List<TagCommit>>() {

            @Override public void onSuccess(List<TagCommit> tags) {
                removeRedundantHead(tags);
                clear();
                for (TagCommit tag : tags) {
                    tag.check();
                    add(tag);
                }
            }

            @Override public void onFailure(Throwable e) {
                Window.alert("Problem fetching commit tags: " + e.toString());
                e.printStackTrace();
            }


        });

//        service.fetchTags(seriesKey, new FetchTagsCallback() {
//            @Override public void onSuccess(List<TagCommit> tags) {
//                removeRedundantHead(tags);
//                sort(tags);
//                clear();
//                for (TagCommit tag : tags) {
//                    add(tag);
//                }
//
//            }
//        });
    }


}
