package c3i.featureModel.shared.dirtyQueue;

/**
 * These events are fired (i.e. added to queue) after the CspState has been updated.
 *
 * So the main point of this queue is to indicate that
 * some inference/simplification
 * MAY be possible as a result
 * of recent changes to CspState the ChangeEvent's
 * currently in the CspChangeQueue
 */
public class CspChangeEvent {
}
