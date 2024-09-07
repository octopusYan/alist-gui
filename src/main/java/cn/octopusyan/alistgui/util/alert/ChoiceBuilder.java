package cn.octopusyan.alistgui.util.alert;

import javafx.scene.control.ChoiceDialog;
import javafx.stage.Window;

import java.util.Collection;
import java.util.Optional;

/**
 * @author octopus_yan
 */
public class ChoiceBuilder<R> extends BaseBuilder<ChoiceDialog<R>, R> {
    public ChoiceBuilder(ChoiceDialog<R> alert, Window mOwner) {
        super(alert, mOwner);
    }

    /**
     * AlertUtil.choices
     */
    public R getChoice(Collection<R> choices) {
        Optional<R> result = alert.showAndWait();
        return result.orElse(null);
    }
}
