/*
 * This class is modified from the PSI mod created by Vazkii
 * Psi Source Code: https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 *
 * HVB007: IDK What Part This credit refers to, if you want to know contact https://github.com/CaelTheColher as he is the maker of this mod
 * I am just updating it to 1.20.x
 */
package net.hvb007.keybindsgalore;

import com.mojang.blaze3d.systems.RenderSystem;
import net.hvb007.keybindsgalore.mixin.AccessorKeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

public class KeybindsScreen extends Screen {

    int timeIn = 0;
    int slotSelected = -1;

    private InputUtil.Key conflictedKey = InputUtil.UNKNOWN_KEY;

    final MinecraftClient mc;

    public KeybindsScreen() {
        super(NarratorManager.EMPTY);
        mc = MinecraftClient.getInstance();
    }

    @Override
    //Updated to use DrawContext instead of MatrixStack
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context,mouseX,mouseY,delta);

        int x = width / 2;
        int y = height / 2;
        int maxRadius = 80;

        double angle = mouseAngle(x, y, mouseX, mouseY);

        //Determines how many segments to make for the circle selector thingy
        int segments = KeybindsManager.getConflicting(conflictedKey).size();
        float step = (float) Math.PI / 180;
        float degPer = (float) Math.PI * 2 / segments;

        slotSelected = -1;

        Tessellator tess = Tessellator.getInstance();// IDK
        BufferBuilder buf = tess.getBuffer();

        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram); //Updated to 1.20
        buf.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        //if cursor is in sector then it Highlights
        for (int seg = 0; seg < segments; seg++) {
            boolean mouseInSector = degPer * seg < angle && angle < degPer * (seg + 1);
            float radius = Math.max(0F, Math.min((timeIn + delta - seg * 6F / segments) * 40F, maxRadius));
            if (mouseInSector) {
                radius *= 1.025f;
            }

            int gs = 0x40;
            if (seg % 2 == 0) {
                gs += 0x19;
            }
            int r = gs;
            int g = gs;
            int b = gs;
            int a = 0x66;

            if (seg == 0) {
                buf.vertex(x, y, 0).color(r, g, b, a).next();
            }

            if (mouseInSector) {
                slotSelected = seg;
                r = g = b = 0xFF;
            }

            for (float i = 0; i < degPer + step / 2; i += step) {
                float rad = i + seg * degPer;
                float xp = x + MathHelper.cos(rad) * radius;
                float yp = y + MathHelper.sin(rad) * radius;

                if (i == 0) {
                    buf.vertex(xp, yp, 0).color(r, g, b, a).next();
                }
                buf.vertex(xp, yp, 0).color(r, g, b, a).next();
            }
        }
        tess.draw();
        // IDK, This does something but im not sure
        for (int seg = 0; seg < segments; seg++) {
            boolean mouseInSector = degPer * seg < angle && angle < degPer * (seg + 1);
            float radius = Math.max(0F, Math.min((timeIn + delta - seg * 6F / segments) * 40F, maxRadius));
            if (mouseInSector) {
                radius *= 1.025f;
            }

            float rad = (seg + 0.5f) * degPer;
            float xp = x + MathHelper.cos(rad) * radius;
            float yp = y + MathHelper.sin(rad) * radius;
            String boundKey = Text.translatable(KeybindsManager.getConflicting(conflictedKey).get(seg).getTranslationKey()).getString();
            float xsp = xp - 4;
            float ysp = yp;
            String name = (mouseInSector ? Formatting.UNDERLINE : Formatting.RESET) + boundKey;
            int width = textRenderer.getWidth(name);
            if (xsp < x) {
                xsp -= width - 8;
            }
            if (ysp < y) {
                ysp -= 9;
            }

            // Updated To 1.20, uses DrawContext instead of textRenderer
            context.drawTextWithShadow(textRenderer,name, (int) xsp, (int) ysp, 0xFFFFFF);

        }
    }


    public void setConflictedKey(InputUtil.Key key) {
        this.conflictedKey = key;
    }

    // Returns the angle of the mouse position relative to inputted x and y
    private static double mouseAngle(int x, int y, int mx, int my) {
        return (MathHelper.atan2(my - y, mx - x) + Math.PI * 2) % (Math.PI * 2);
    }

    @Override
    //Checks for Conflicted keys every gametick and waits for input to press selected key once.
    public void tick() {
        super.tick();
        if (!InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), conflictedKey.getCode())) {
            mc.setScreen(null);
            if (slotSelected != -1) {
                KeyBinding bind = KeybindsManager.getConflicting(conflictedKey).get(slotSelected);
                ((AccessorKeyBinding) bind).setPressed(true);
                ((AccessorKeyBinding) bind).setTimesPressed(1);
            }
        }
        timeIn++;
    }

    @Override
    // IDK
    public boolean shouldPause() {
        return false;
    }
}
