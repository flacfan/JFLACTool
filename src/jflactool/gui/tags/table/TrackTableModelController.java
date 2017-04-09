package jflactool.gui.tags.table;

import javax.swing.event.TableModelListener;
import jflactool.gui.action_buttons.ActionButtonsPanel;
import jflactool.gui.tags.TagsPanel;
import jflactool.gui.tags.fields.TagsModel;

public class TrackTableModelController
{
    private TagsModel tagsModel;
    private ActionButtonsPanel actionButtonsPanel;
    private TagsPanel tagsPanel;

    private TableModelListener tableModelListener;

    public TrackTableModelController(TagsModel tagsModel,
            ActionButtonsPanel actionButtonsPanel,
            TagsPanel tagsPanel)
    {
        this.tagsModel = tagsModel;
        this.actionButtonsPanel = actionButtonsPanel;
        this.tagsPanel = tagsPanel;
        connectController();
    }

    private void connectController()
    {
        tableModelListener = tableModelEvent ->
        {
            if (tableModelEvent.getColumn() <= 0
                    && !tagsModel.haveSelectedFiles())
            {
                actionButtonsPanel.getSpectrogramButton().setEnabled(false);
                actionButtonsPanel.getArchiveButton().setEnabled(false);
                actionButtonsPanel.getArchiveComboBox().setEnabled(false);
                actionButtonsPanel.getConvertButton().setEnabled(false);
            }

            else if (tableModelEvent.getColumn() <= 0
                    && tagsModel.haveSelectedFiles())
            {
                actionButtonsPanel.getSpectrogramButton().setEnabled(true);
                actionButtonsPanel.getArchiveButton().setEnabled(true);
                actionButtonsPanel.getArchiveComboBox().setEnabled(true);
                actionButtonsPanel.getConvertButton().setEnabled(true);
            }
        };

        tagsPanel.getTrackTable().getModel().addTableModelListener(
                tableModelListener);
    }
}