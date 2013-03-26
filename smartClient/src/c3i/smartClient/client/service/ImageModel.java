package c3i.smartClient.client.service;

import c3i.imageModel.shared.api.SimplePicks;

/**
 *      Synchronous
 *      Single imageModel:
 *          only covers a *single* imageModel
 *          which was presumably loaded and parsed
 *          prior to arriving here (in order to have created this IM)
 *
 *
 */
public interface ImageModel {

    String[] getPngs(String view, int angle, SimplePicks picks);

}
