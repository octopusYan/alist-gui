package cn.octopusyan.alistgui.view.alert.builder;

import cn.octopusyan.alistgui.base.BaseBuilder;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Window;

import java.util.Optional;

/**
 * @author octopus_yan
 */
public class ChoiceBuilder<R> extends BaseBuilder<ChoiceBuilder<R>, ChoiceDialog<R>> {

    @SafeVarargs
    public ChoiceBuilder(Window mOwner, R... choices) {
        this(new ChoiceDialog<>(choices[0], choices), mOwner);
    }

    public ChoiceBuilder(ChoiceDialog<R> dialog, Window mOwner) {
        super(dialog, mOwner);
    }

    /**
     * AlertUtil.choices
     */
    public R showAndGetChoice() {
        Optional<R> result = dialog.showAndWait();
        return result.orElse(null);
    }
}
