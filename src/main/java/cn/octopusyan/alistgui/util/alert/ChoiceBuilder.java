package cn.octopusyan.alistgui.util.alert;

import javafx.scene.control.ChoiceDialog;
import javafx.stage.Window;

import java.util.Collection;
import java.util.Optional;

/**
 * @author octopus_yan
 */
public class ChoiceBuilder<R> extends BaseBuilder<ChoiceBuilder<R>, ChoiceDialog<R>> {
    public ChoiceBuilder(ChoiceDialog<R> dialog, Window mOwner) {
        super(dialog, mOwner);
    }

    /**
     * AlertUtil.choices
     */
    public R getChoice(Collection<R> choices) {
        Optional<R> result = dialog.showAndWait();
        return result.orElse(null);
    }
}
