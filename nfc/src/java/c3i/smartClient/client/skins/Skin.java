package c3i.smartClient.client.skins;

import c3i.smartClient.client.model.ViewModel;
import com.google.gwt.user.client.ui.IsWidget;

public interface Skin {

    IsWidget createPreviewPanel(ViewModel viewModel);

    String getSkinName();


}
