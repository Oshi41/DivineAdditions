package divineadditions.jei;

import divineadditions.DivineAdditions;
import divineadditions.gui.gui_container.ForgeGuiContainer;
import mezz.jei.api.gui.IGhostIngredientHandler;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredientRenderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ForgeGhostIngredientHandler implements IGhostIngredientHandler<ForgeGuiContainer> {
    private IIngredientRegistry registry;

    public ForgeGhostIngredientHandler(IIngredientRegistry registry) {
        this.registry = registry;
    }

    @Override
    public <I> List<Target<I>> getTargets(ForgeGuiContainer forgeGuiContainer, I ingredient, boolean doStart) {
        ArrayList<Target<I>> targets = new ArrayList<>();
        targets.add(new SlotTarget<>(new Rectangle(5, 84 + 24, 20, 20)));
        return targets;
    }

    @Override
    public void onComplete() {
        DivineAdditions.logger.debug("smth happened");
    }

    public class SlotTarget<T> implements IGhostIngredientHandler.Target<T> {

        private final Rectangle area;

        public SlotTarget(Rectangle area) {
            this.area = area;
        }

        @Override
        public Rectangle getArea() {
            return area;
        }

        @Override
        public void accept(T o) {
            IIngredientHelper<T> helper = registry.getIngredientHelper(o);
            IIngredientRenderer<T> renderer = registry.getIngredientRenderer(o);
            DivineAdditions.logger.debug("smth happened");
        }
    }
}
