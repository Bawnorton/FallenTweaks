package com.bawnorton.multitweaks.gui.widgets;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.mojang.blaze3d.platform.GlStateManager.LogicOp;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import java.util.function.Consumer;
import java.util.function.Predicate;

import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class MWTextField extends WWidget {
    public static final int OFFSET_X_TEXT = 4;
    @Environment(EnvType.CLIENT)
    private TextRenderer font;
    protected String text = "";
    protected int maxLength = 16;
    protected boolean editable = true;
    protected int enabledColor = 14737632;
    protected int uneditableColor = 7368816;
    @Nullable
    protected Text suggestion = null;
    protected int cursor = 0;
    protected int select = -1;
    protected Consumer<String> onChanged;
    protected Predicate<String> textPredicate;
    @Environment(EnvType.CLIENT)
    @Nullable
    protected BackgroundPainter backgroundPainter;

    public MWTextField() {
    }

    public MWTextField(Text suggestion) {
        this.suggestion = suggestion;
    }

    public void setText(String s) {
        if (this.textPredicate == null || this.textPredicate.test(s)) {
            this.text = s.length() > this.maxLength ? s.substring(0, this.maxLength) : s;
            if (this.onChanged != null) {
                this.onChanged.accept(this.text);
            }
        }

    }

    public String getText() {
        return this.text;
    }

    public boolean canResize() {
        return true;
    }

    public void setSize(int x, int y) {
        super.setSize(x, 20);
    }

    public void setCursorPos(int location) {
        this.cursor = MathHelper.clamp(location, 0, this.text.length());
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public int getCursor() {
        return this.cursor;
    }

    @Nullable
    public String getSelection() {
        if (this.select < 0) {
            return null;
        } else if (this.select == this.cursor) {
            return null;
        } else {
            if (this.select > this.text.length()) {
                this.select = this.text.length();
            }

            if (this.cursor < 0) {
                this.cursor = 0;
            }

            if (this.cursor > this.text.length()) {
                this.cursor = this.text.length();
            }

            int start = Math.min(this.select, this.cursor);
            int end = Math.max(this.select, this.cursor);
            return this.text.substring(start, end);
        }
    }

    public boolean isEditable() {
        return this.editable;
    }

    @Environment(EnvType.CLIENT)
    protected void renderTextField(MatrixStack matrices, int x, int y) {
        if (this.font == null) {
            this.font = MinecraftClient.getInstance().textRenderer;
        }

        int borderColor = this.isFocused() ? -96 : -6250336;
        ScreenDrawing.coloredRect(x - 1, y - 1, this.width + 2, this.height + 2, borderColor);
        ScreenDrawing.coloredRect(x, y, this.width, this.height, -16777216);
        int textColor = this.editable ? this.enabledColor : this.uneditableColor;
        String trimText = this.font.trimToWidth(this.text, this.width - 4, true);
        boolean selection = this.select != -1;
        boolean focused = this.isFocused();
        int textX = x + 4;
        int textY = y + (this.height - 8) / 2;
        int adjustedCursor = this.cursor;
        if (adjustedCursor > trimText.length()) {
            adjustedCursor = trimText.length();
        }

        int preCursorAdvance = textX;
        if (!trimText.isEmpty()) {
            String string_2 = trimText.substring(0, adjustedCursor);
            preCursorAdvance = this.font.drawWithShadow(matrices, string_2, (float)textX, (float)textY, textColor);
        }

        if (adjustedCursor < trimText.length()) {
            this.font.drawWithShadow(matrices, trimText.substring(adjustedCursor), (float)(preCursorAdvance - 1), (float)textY, textColor);
        }

        if (this.text.length() == 0 && this.suggestion != null) {
            this.font.drawWithShadow(matrices, this.suggestion, (float)textX, (float)textY, -8355712);
        }

        if (focused && !selection) {
            if (adjustedCursor < trimText.length()) {
                ScreenDrawing.coloredRect(preCursorAdvance - 1, textY - 2, 1, 12, -3092272);
            } else {
                this.font.drawWithShadow(matrices, "_", (float)preCursorAdvance, (float)textY, textColor);
            }
        }

        if (selection) {
            int a = getCaretOffset(this.text, this.cursor);
            int b = getCaretOffset(this.text, this.select);
            if (b < a) {
                int tmp = b;
                b = a;
                a = tmp;
            }

            this.invertedRect(textX + a - 1, textY - 1, Math.min(b - a, this.width - 4), 12);
        }

    }

    @Environment(EnvType.CLIENT)
    private void invertedRect(int x, int y, int width, int height) {
        Tessellator tessellator_1 = Tessellator.getInstance();
        BufferBuilder bufferBuilder_1 = tessellator_1.getBuffer();
        RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(LogicOp.OR_REVERSE);
        bufferBuilder_1.begin(7, VertexFormats.POSITION);
        bufferBuilder_1.vertex((double)x, (double)(y + height), 0.0D).next();
        bufferBuilder_1.vertex((double)(x + width), (double)(y + height), 0.0D).next();
        bufferBuilder_1.vertex((double)(x + width), (double)y, 0.0D).next();
        bufferBuilder_1.vertex((double)x, (double)y, 0.0D).next();
        tessellator_1.draw();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    public MWTextField setTextPredicate(Predicate<String> predicate_1) {
        this.textPredicate = predicate_1;
        return this;
    }

    public MWTextField setChangedListener(Consumer<String> listener) {
        this.onChanged = listener;
        return this;
    }

    public MWTextField setMaxLength(int max) {
        this.maxLength = max;
        if (this.text.length() > max) {
            this.text = this.text.substring(0, max);
            this.onChanged.accept(this.text);
        }

        return this;
    }

    public MWTextField setEnabledColor(int col) {
        this.enabledColor = col;
        return this;
    }

    public MWTextField setDisabledColor(int col) {
        this.uneditableColor = col;
        return this;
    }

    public MWTextField setEditable(boolean editable) {
        this.editable = editable;
        return this;
    }

    @Nullable
    public Text getSuggestion() {
        return this.suggestion;
    }

    public MWTextField setSuggestion(@Nullable String suggestion) {
        this.suggestion = suggestion != null ? new LiteralText(suggestion) : null;
        return this;
    }

    public MWTextField setSuggestion(@Nullable Text suggestion) {
        this.suggestion = suggestion;
        return this;
    }

    @Environment(EnvType.CLIENT)
    public MWTextField setBackgroundPainter(BackgroundPainter painter) {
        this.backgroundPainter = painter;
        return this;
    }

    public boolean canFocus() {
        return true;
    }

    public void onFocusGained() {
    }

    @Environment(EnvType.CLIENT)
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        this.renderTextField(matrices, x, y);
    }

    @Environment(EnvType.CLIENT)
    public void onClick(int x, int y, int button) {
        this.requestFocus();
        this.cursor = getCaretPos(this.text, x - 4);
    }

    @Environment(EnvType.CLIENT)
    public void onCharTyped(char ch) {
        if (this.text.length() < this.maxLength) {
            if (this.cursor < 0) {
                this.cursor = 0;
            }

            if (this.cursor > this.text.length()) {
                this.cursor = this.text.length();
            }

            String before = this.text.substring(0, this.cursor);
            String after = this.text.substring(this.cursor, this.text.length());
            this.text = before + ch + after;
            ++this.cursor;
        }

    }

    public void insertText(int ofs, String s) {
    }

    @Environment(EnvType.CLIENT)
    public void onKeyPressed(int ch, int key, int modifiers) {
        if (this.editable) {
            String before;
            if (Screen.isCopy(ch)) {
                before = this.getSelection();
                if (before != null) {
                    MinecraftClient.getInstance().keyboard.setClipboard(before);
                }

            } else {
                String after;
                int tmp;
                int a;
                int b;
                if (Screen.isPaste(ch)) {
                    if (this.select != -1) {
                        a = this.select;
                        b = this.cursor;
                        if (b < a) {
                            tmp = b;
                            b = a;
                            a = tmp;
                        }

                        before = this.text.substring(0, a);
                        after = this.text.substring(b);
                        String clip = MinecraftClient.getInstance().keyboard.getClipboard();
                        this.text = before + clip + after;
                        this.select = -1;
                        this.cursor = (before + clip).length();
                    } else {
                        before = this.text.substring(0, this.cursor);
                        after = this.text.substring(this.cursor, this.text.length());
                        before = MinecraftClient.getInstance().keyboard.getClipboard();
                        this.text = before + before + after;
                        this.cursor += before.length();
                        if (this.text.length() > this.maxLength) {
                            this.text = this.text.substring(0, this.maxLength);
                            if (this.cursor > this.text.length()) {
                                this.cursor = this.text.length();
                            }
                        }
                    }

                } else if (Screen.isSelectAll(ch)) {
                    this.select = 0;
                    this.cursor = this.text.length();
                } else {
                    if (modifiers == 0) {
                        if (ch != 261 && ch != 259) {
                            if (ch == 263) {
                                if (this.select != -1) {
                                    this.cursor = Math.min(this.cursor, this.select);
                                    this.select = -1;
                                } else if (this.cursor > 0) {
                                    --this.cursor;
                                }
                            } else if (ch == 262) {
                                if (this.select != -1) {
                                    this.cursor = Math.max(this.cursor, this.select);
                                    this.select = -1;
                                } else if (this.cursor < this.text.length()) {
                                    ++this.cursor;
                                }
                            }
                        } else if (this.text.length() > 0 && this.cursor > 0) {
                            if (this.select >= 0 && this.select != this.cursor) {
                                a = this.select;
                                b = this.cursor;
                                if (b < a) {
                                    tmp = b;
                                    b = a;
                                    a = tmp;
                                }

                                before = this.text.substring(0, a);
                                after = this.text.substring(b);
                                this.text = before + after;
                                if (this.cursor == b) {
                                    this.cursor = a;
                                }

                                this.select = -1;
                            } else {
                                before = this.text.substring(0, this.cursor);
                                after = this.text.substring(this.cursor, this.text.length());
                                before = before.substring(0, before.length() - 1);
                                this.text = before + after;
                                --this.cursor;
                            }
                        }
                    } else if (modifiers == 1) {
                        if (ch == 263) {
                            if (this.select == -1) {
                                this.select = this.cursor;
                            }

                            if (this.cursor > 0) {
                                --this.cursor;
                            }

                            if (this.select == this.cursor) {
                                this.select = -1;
                            }
                        } else if (ch == 262) {
                            if (this.select == -1) {
                                this.select = this.cursor;
                            }

                            if (this.cursor < this.text.length()) {
                                ++this.cursor;
                            }

                            if (this.select == this.cursor) {
                                this.select = -1;
                            }
                        }
                    }

                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static int getCaretPos(String s, int x) {
        if (x <= 0) {
            return 0;
        } else {
            TextRenderer font = MinecraftClient.getInstance().textRenderer;
            int lastAdvance = 0;

            for(int i = 0; i < s.length() - 1; ++i) {
                int advance = font.getWidth(s.substring(0, i + 1));
                int charAdvance = advance - lastAdvance;
                if (x < advance + charAdvance / 2) {
                    return i + 1;
                }

                lastAdvance = advance;
            }

            return s.length();
        }
    }

    @Environment(EnvType.CLIENT)
    public static int getCaretOffset(String s, int pos) {
        if (pos == 0) {
            return 0;
        } else {
            TextRenderer font = MinecraftClient.getInstance().textRenderer;
            int ofs = font.getWidth(s.substring(0, pos)) + 1;
            return ofs;
        }
    }
}
