package net.bdavies.display.ui;

import static imgui.flag.ImGuiWindowFlags.*;

import java.awt.*;
import java.util.Set;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bdavies.api.IApplication;
import net.bdavies.api.IDisplay;
import net.bdavies.api.strip.IStrip;
import net.bdavies.fx.EffectRegistry;

/**
 * The UI Renderer for the debug window
 *
 * @author ben.davies
 */
@Slf4j
@RequiredArgsConstructor
public class UIRenderer
{
	private final IDisplay display;
	private final IApplication application;
	private IStrip currentSelected = null;
	public void render()
	{
		if (currentSelected == null) {
			currentSelected = application.getStrips().get(0);
		}
		int windowFlags = NoMove | NoDocking | NoDecoration;
		ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
		ImGui.setNextWindowPos(200, 0);
		ImGui.setNextWindowSize(display.getWidth() - 200, display.getHeight());
		ImGui.begin("Test", windowFlags);
		val draw_list = ImGui.getWindowDrawList();
		int pixelCount = currentSelected.getPixelCount();
		float pixelSize = (float) Math.ceil(((float) (display.getWidth() - 200)) / (float) pixelCount);
		for (int i = 0; i < pixelCount; i++)
		{
			Color c = new Color(currentSelected.getColorAtPixel(i), true);
			Color c2 = new Color(c.getBlue(), c.getGreen(), c.getRed(), currentSelected.getBrightness());
			int color = c2.getRGB();
			int padding = 0;
			float leftX = 200 + padding + (i * pixelSize);
			float rightX = 200 - padding + (i * pixelSize) + pixelSize;
			draw_list.addQuadFilled(
					leftX, 0f, //Top Left
					leftX, display.getHeight(), //Bottom Left
					rightX, display.getHeight(), //Bottom Right
					rightX, 0L, //Top Right
					color);
		}
		ImGui.end();
		//        ImGui.showDemoWindow();
		ImGui.setNextWindowPos(0, 0);
		ImGui.setNextWindowSize(200, display.getHeight());
		ImGui.begin("Test2", windowFlags);
		ImGui.popStyleVar();
		ImGui.pushStyleColor(ImGuiCol.FrameBg, 0xFF000000);
		ImGui.text("Strips:");
		if (ImGui.beginListBox("##listbox", 200, 90))
		{
			for (IStrip item : application.getStrips())
			{
				if (ImGui.selectable(item.getName(), currentSelected.equals(item)))
				{
					log.debug("Current Viewing strip changed to: {}", item.getName());
					currentSelected = item;
				}
			}
			ImGui.endListBox();
		}
		Set<String> fx = EffectRegistry.getFormattedNames();
		ImGui.text("Effects:");
		if (ImGui.beginListBox("##listbox2", 200, 90))
		{
			for (String item : fx)
			{
				val ef = currentSelected.getEffect();
				val name = ef != null ? EffectRegistry.getEffectName(ef.getClass()) : "";
				if (ImGui.selectable(item, name.equalsIgnoreCase(item)))
				{
						currentSelected.setEffect(EffectRegistry.getEffect(item.toLowerCase()));
				}
			}
			ImGui.endListBox();
		}
		ImGui.popStyleColor();
		val drawList = ImGui.getWindowDrawList();
		drawList.addText(10, display.getHeight() - 30, 0xFFFFFFFF, "You are currently viewing:\n\t  " + currentSelected.getName());
		ImGui.end();
	}
}
