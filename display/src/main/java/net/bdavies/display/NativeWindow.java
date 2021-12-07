package net.bdavies.display;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.*;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.lwjgl.Version;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.MemoryStack;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.IApplication;
import net.bdavies.api.IDisplay;
import net.bdavies.display.ui.UIRenderer;

/**
 * Native window implementation for the IDisplay interface
 *
 * It uses OpenGL, GLFW and ImGui to render the strip pixels for easy debugging
 *
 * @author ben.davies
 */
@Slf4j
public class NativeWindow implements IDisplay, Runnable
{
	private final ImGuiImplGlfw imGlfw = new ImGuiImplGlfw();
	private final ImGuiImplGl3 imGl = new ImGuiImplGl3();

	private final AtomicLong windowPtr = new AtomicLong(NULL);
	private Thread windowThread;
	private final UIRenderer uiRenderer;
	private final AtomicReference<Rectangle> windowPos = new AtomicReference<>(new Rectangle(0, 0, 800, 600));
	private final IApplication application;

	/**
	 * Create a native window
	 *
	 * @param application The application class
	 */
	public NativeWindow(IApplication application)
	{
		this.application = application;
		this.uiRenderer = new UIRenderer(this, application);
	}

	/**
	 * Init the window creating a thread for the window to run on
	 */
	@Override
	public void init()
	{
		if (windowThread == null) {
			windowThread = new Thread(this, "NativeDisplay");
			windowThread.start();
		}
	}

	/**
	 * Get the width of the window
	 *
	 * @return int
	 */
	@Override
	public int getWidth()
	{
		return windowPos.get().width;
	}

	/**
	 * Get the height of the window
	 *
	 * @return int
	 */
	@Override
	public int getHeight()
	{
		return windowPos.get().height;
	}

	/**
	 * Get the pos X of the window
	 *
	 * @return int
	 */
	@Override
	public int getPosX()
	{
		return windowPos.get().x;
	}

	/**
	 * Get the pos Y of the window
	 *
	 * @return int
	 */
	@Override
	public int getPosY()
	{
		return windowPos.get().y;
	}

	/**
	 * When an object implementing interface {@code Runnable} is used
	 * to create a thread, starting the thread causes the object's
	 * {@code run} method to be called in that separately executing
	 * thread.
	 * <p>
	 * The general contract of the method {@code run} is that it may
	 * take any action whatsoever.
	 *
	 * @see Thread#run()
	 */
	@Override
	public void run()
	{
		log.info("LWJGL Version: {}", Version.getVersion());
		makeWindow();
		GL.createCapabilities();
		setupImGui();
		loop();
		destroy();
		application.shutdown();
	}

	/**
	 * Render elements to the screen
	 */
	protected void render() {

	}

	/**
	 * The run loop for the window
	 */
	private void loop() {
		while ( !glfwWindowShouldClose(windowPtr.get()) ) {
			glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			render();
			renderImGui();
			glfwSwapBuffers(windowPtr.get());
			glfwPollEvents();
		}
	}

	/**
	 * Render the ImGui ui
	 */
	private void renderImGui()
	{
		 imGlfw.newFrame();
		 ImGui.newFrame();
		 uiRenderer.render();
		 ImGui.render();
		 imGl.renderDrawData(ImGui.getDrawData());

		 if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
			 final long backupWindowPtr = glfwGetCurrentContext();
			 ImGui.updatePlatformWindows();
			 ImGui.renderPlatformWindowsDefault();
			 glfwMakeContextCurrent(backupWindowPtr);
		 }
	}

	/**
	 * Make the GLFW Window
	 */
	private void makeWindow() {
		Map<Integer, String> ERROR_CODES =
				APIUtil.apiClassTokens((field, value) -> 0x10000 < value && value < 0x20000, null, GLFW.class);
		GLFWErrorCallback.create((err, descr) ->
				log.error("GLFW Error: {} - {}", ERROR_CODES.get(err), descr));

		if(!glfwInit()) {
			log.error("Unable to init GLFW!");
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		glfwWindowHint(GLFW_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_VERSION_MINOR, 0);

		// Create the window
		log.trace("Making the GLFW Window");
		windowPtr.set(glfwCreateWindow(1280, 250, "Strip Viewer", NULL, NULL));
		if ( windowPtr.get() == NULL ) {
			log.error("Unable to create the window");
		}

		try ( MemoryStack stack = stackPush() )
		{
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);

			glfwGetWindowSize(windowPtr.get(), pWidth, pHeight);

			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			assert vidmode != null;
			int xpos = (vidmode.width() - pWidth.get(0)) / 2;
			int ypos = (vidmode.height() - pHeight.get(0)) / 2;
			glfwSetWindowPos(windowPtr.get(), xpos, ypos);
			windowPos.set(new Rectangle(xpos, ypos, pWidth.get(0), pHeight.get(0)));
		}
		glfwMakeContextCurrent(windowPtr.get());
		glfwShowWindow(windowPtr.get());
	}

	/**
	 * Setup the ImGui System
	 */
	private void setupImGui() {
		ImGui.createContext();
		imGlfw.init(windowPtr.get(), true);
		imGl.init();
	}

	/**
	 * Destroy the window
	 */
	private void destroy() {
		log.trace("Destroying the GLFW window");
		imGlfw.dispose();
		imGl.dispose();
		ImGui.destroyContext();
		Callbacks.glfwFreeCallbacks(windowPtr.get());
		glfwDestroyWindow(windowPtr.get());
		glfwTerminate();
	}
}
