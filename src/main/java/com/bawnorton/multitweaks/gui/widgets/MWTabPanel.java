package com.bawnorton.multitweaks.gui.widgets;


import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class MWTabPanel extends WPanel {
    private final WBox tabRibbon;
    private final List<MWTabPanel.WTab> tabWidgets;
    private final WCardPanel mainPanel;

    public MWTabPanel() {
        this.tabRibbon = (new WBox(Axis.HORIZONTAL)).setSpacing(1);
        this.tabWidgets = new ArrayList<>();
        this.mainPanel = new WCardPanel();
        this.add(this.tabRibbon, 0, 0);
        this.add(this.mainPanel, 8, 38);
    }

    private void add(WWidget widget, int x, int y) {
        this.children.add(widget);
        widget.setParent(this);
        widget.setLocation(x, y);
        this.expandToFit(widget);
    }

    public void add(MWTabPanel.Tab tab) {
        MWTabPanel.WTab tabWidget = new MWTabPanel.WTab(tab);
        if (this.tabWidgets.isEmpty()) {
            tabWidget.selected = true;
        }

        this.tabWidgets.add(tabWidget);
        this.tabRibbon.add(tabWidget, 28, 20);
        this.mainPanel.add(tab.getWidget());
    }

    public void add(WWidget widget, Consumer<MWTabPanel.Tab.Builder> configurator) {
        MWTabPanel.Tab.Builder builder = new MWTabPanel.Tab.Builder(widget);
        configurator.accept(builder);
        this.add(builder.build());
    }

    public void setSize(int x, int y) {
        super.setSize(x, y);
        this.tabRibbon.setSize(x, 30);
    }

    @Environment(EnvType.CLIENT)
    public void addPainters() {
        super.addPainters();
        this.mainPanel.setBackgroundPainter(BackgroundPainter.VANILLA);
    }

    public static class Tab {
        @Nullable
        private final Text title;
        @Nullable
        private final Icon icon;
        private final WWidget widget;
        @Nullable
        private final Consumer<TooltipBuilder> tooltip;

        /**
         * @deprecated
         */
        @Deprecated
        public Tab(@Nullable Text title, @Nullable Icon icon, WWidget widget, @Nullable Consumer<TooltipBuilder> tooltip) {
            if (title == null && icon == null) {
                throw new IllegalArgumentException("A tab must have a title or an icon");
            } else {
                this.title = title;
                this.icon = icon;
                this.widget = Objects.requireNonNull(widget, "widget");
                this.tooltip = tooltip;
            }
        }

        @Nullable
        public Text getTitle() {
            return this.title;
        }

        @Nullable
        public Icon getIcon() {
            return this.icon;
        }

        public WWidget getWidget() {
            return this.widget;
        }

        @Environment(EnvType.CLIENT)
        public void addTooltip(TooltipBuilder tooltip) {
            if (this.tooltip != null) {
                this.tooltip.accept(tooltip);
            }

        }

        public static final class Builder {
            private final WWidget widget;
            private final List<Text> tooltip = new ArrayList<>();
            @Nullable
            private Text title;
            @Nullable
            private Icon icon;

            public Builder(WWidget widget) {
                this.widget = Objects.requireNonNull(widget, "widget");
            }

            public void title(Text title) {
                this.title = Objects.requireNonNull(title, "title");
            }

            public MWTabPanel.Tab.Builder icon(Icon icon) {
                this.icon = Objects.requireNonNull(icon, "icon");
                return this;
            }

            public MWTabPanel.Tab.Builder tooltip(Text... lines) {
                Objects.requireNonNull(lines, "lines");
                Collections.addAll(this.tooltip, lines);
                return this;
            }

            public MWTabPanel.Tab.Builder tooltip(Collection<? extends Text> lines) {
                Objects.requireNonNull(lines, "lines");
                this.tooltip.addAll(lines);
                return this;
            }

            public MWTabPanel.Tab build() {
                Consumer<TooltipBuilder> tooltip = null;
                if (!this.tooltip.isEmpty()) {
                    tooltip = builder -> builder.add(Builder.this.tooltip.toArray(new Text[0]));
                }

                return new MWTabPanel.Tab(this.title, this.icon, this.widget, tooltip);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    static final class Painters {
        static final BackgroundPainter SELECTED_TAB = BackgroundPainter.createLightDarkVariants(BackgroundPainter.createNinePatch(new Identifier("libgui", "textures/widget/tab/selected_light.png")).setTopPadding(2), BackgroundPainter.createNinePatch(new Identifier("libgui", "textures/widget/tab/selected_dark.png")).setTopPadding(2));
        static final BackgroundPainter UNSELECTED_TAB = BackgroundPainter.createLightDarkVariants(BackgroundPainter.createNinePatch(new Identifier("libgui", "textures/widget/tab/unselected_light.png")), BackgroundPainter.createNinePatch(new Identifier("libgui", "textures/widget/tab/unselected_dark.png")));
        static final BackgroundPainter SELECTED_TAB_FOCUS_BORDER = BackgroundPainter.createNinePatch(new Identifier("libgui", "textures/widget/tab/focus.png")).setTopPadding(2);
        static final BackgroundPainter UNSELECTED_TAB_FOCUS_BORDER = BackgroundPainter.createNinePatch(new Identifier("libgui", "textures/widget/tab/focus.png"));

        Painters() {
        }
    }

    private final class WTab extends WWidget {
        private final MWTabPanel.Tab data;
        boolean selected = false;

        WTab(MWTabPanel.Tab data) {
            this.data = data;
        }

        public boolean canFocus() {
            return true;
        }

        @Environment(EnvType.CLIENT)
        public void onClick(int x, int y, int button) {
            super.onClick(x, y, button);
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));

            MWTabPanel.WTab tab;
            for (Iterator<MWTabPanel.WTab> var4 = MWTabPanel.this.tabWidgets.iterator(); var4.hasNext(); tab.selected = tab == this) {
                tab = var4.next();
            }

            MWTabPanel.this.mainPanel.setSelectedCard(this.data.getWidget());
            MWTabPanel.this.layout();
        }

        @Environment(EnvType.CLIENT)
        public void onKeyPressed(int ch, int key, int modifiers) {
            if (isActivationKey(ch)) {
                this.onClick(0, 0, 0);
            }

        }

        @Environment(EnvType.CLIENT)
        public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
            Text title = this.data.getTitle();
            Icon icon = this.data.getIcon();
            if (title != null) {
                int widthx = 28 + renderer.getWidth(title);
                if (icon == null) {
                    widthx = Math.max(28, widthx - 16);
                }

                if (this.width != widthx) {
                    this.setSize(widthx, this.height);
                    Objects.requireNonNull(this.getParent()).layout();
                }
            }

            (this.selected ? MWTabPanel.Painters.SELECTED_TAB : MWTabPanel.Painters.UNSELECTED_TAB).paintBackground(x, y, this);
            if (this.isFocused()) {
                (this.selected ? MWTabPanel.Painters.SELECTED_TAB_FOCUS_BORDER : MWTabPanel.Painters.UNSELECTED_TAB_FOCUS_BORDER).paintBackground(x, y, this);
            }

            int iconX = 6;
            if (title != null) {
                int titleX = icon != null ? iconX + 16 + 1 : 0;
                int var10000 = this.height - 4;
                Objects.requireNonNull(renderer);
                int titleY = (var10000 - 9) / 2 + 1;
                int width = icon != null ? this.width - iconX - 16 : this.width;
                HorizontalAlignment align = icon != null ? HorizontalAlignment.LEFT : HorizontalAlignment.CENTER;
                int color;
                if (LibGuiClient.config.darkMode) {
                    color = 12369084;
                } else {
                    color = this.selected ? 4210752 : 15658734;
                }

                ScreenDrawing.drawString(matrices, title.asOrderedText(), align, x + titleX, y + titleY, width, color);
            }

            if (icon != null) {
                icon.paint(matrices, x + iconX, y + (this.height - 4 - 16) / 2, 16);
            }

        }

        public void addTooltip(TooltipBuilder tooltip) {
            this.data.addTooltip(tooltip);
        }
    }
}
