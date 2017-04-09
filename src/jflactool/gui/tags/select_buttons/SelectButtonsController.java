package jflactool.gui.tags.select_buttons;

import java.awt.event.ActionListener;
import javax.swing.table.AbstractTableModel;
import jflactool.gui.action_buttons.ActionButtonsPanel;
import jflactool.gui.tags.TagsPanel;
import jflactool.gui.tags.fields.TagsModel;

public class SelectButtonsController
{
    private TagsModel tagsModel;
    private ActionButtonsPanel actionButtonsPanel;
    private TagsPanel tagsPanel;

    private ActionListener actionListener;

    public SelectButtonsController(TagsModel tagsModel,
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
        actionListener = actionEvent ->
        {
            if (actionEvent.getSource() == tagsPanel.getSelectAll())
            {
                tagsModel.getMusicFiles().stream().forEach(musicFile ->
                {
                    musicFile.setSelected(Boolean.TRUE);
                });
                ((AbstractTableModel) tagsPanel.getTrackTable()
                        .getModel()).fireTableDataChanged();
            }

            else if (actionEvent.getSource() == tagsPanel.getSelectNone())
            {
                tagsModel.getMusicFiles().stream().forEach(musicFile ->
                {
                    musicFile.setSelected(Boolean.FALSE);
                });
                ((AbstractTableModel) tagsPanel.getTrackTable()
                        .getModel()).fireTableDataChanged();
            }
        };

        tagsPanel.getSelectAll().addActionListener(actionListener);
        tagsPanel.getSelectNone().addActionListener(actionListener);
    }
}