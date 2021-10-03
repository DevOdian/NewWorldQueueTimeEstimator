package OCR.newworld.screenshot;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Slightly changed version of https://stackoverflow.com/a/47182139
 * Handles screenshotting of specific app windows
 */
public class ScreenshotHandler {

    private static int fileCounter = 0;

    public static int getFileCounter() {
        return fileCounter;
    }

    private static void setFileCounter(int fileCounter) {
        ScreenshotHandler.fileCounter = fileCounter;
    }

    /**
     * Method to take a screenshot of an app window cropped referring to parameters
     * @param appWindowName contains the case-sensitive name of the window. The explicit name can be figured using the static method listAllWindows.
     * @param relativePath contains the relativePath to the root screenshot folder to save the screenshot in.
     * @param maxFiles states the maximum amount of files that can be made during one entire session.
     *                 -1 means there is no maximum amount specified.
     * @param leftPointOffsetFactor The higher the factor the more is the left point offsetting to the right. [0;1]
     *                              0.0 resembles the original position of the left point of the window.
     *                              1.0 resembles the position of the right point of the window.
     * @param topPointOffsetFactor The higher the factor the more is the top point offsetting to the bottom. [0;1]
     *                             0.0 resembles the original position of the top point of the window.
     *                             1.0 resembles the position of the bottom point of the window.
     * @param widthFactor The lower the factor the smaller is the width. [0;1]
     *                    0.0 resembles no width.
     *                    1.0 resembles the original width of the window.
     * @param heightFactor The lower the factor the smaller the height. [0;1]
     *                     0.0 resembles no height.
     *                     1.0 resembles the original height of the window.
     * @return File reference to the just made screenshot.
     */
    public static File takeScreenshotOfApp(String appWindowName, String relativePath, int maxFiles, double leftPointOffsetFactor, double topPointOffsetFactor, double widthFactor, double heightFactor) {
        //listAllWindows();

        int hWnd = User32.instance.FindWindowA(null, appWindowName);
        WindowInfo w = getWindowInfo(hWnd);
        if(!w.title.isEmpty()) {
            User32.instance.SetForegroundWindow(w.hwnd);
            try {
                Files.createDirectories(Paths.get(relativePath));
            } catch (IOException ignored) {}
            File file = new File(relativePath + "\\" + appWindowName + "_" + (maxFiles != -1 ? getFileCounter()%maxFiles : getFileCounter()) + ".jpg");
            setFileCounter(fileCounter+1);
            try {
                int left = w.rect.left, right = w.rect.right, top = w.rect.top, bottom = w.rect.bottom,
                           width = (right - left), height = (bottom - top);
                width = map(width, 1920);
                height = map(height, 1080);
                BufferedImage createScreenCapture =
                        new Robot().createScreenCapture(new Rectangle(
                                (int) (left+(width*leftPointOffsetFactor)),
                                (int) (top+(height*topPointOffsetFactor)),
                                (int) (width*widthFactor),
                                (int) (height*heightFactor)));
                ImageIO.write(createScreenCapture, "jpg", file);

                resize(file, createScreenCapture.getWidth()*3, createScreenCapture.getHeight()*3);
            } catch (Exception ignored) {
            }
            return file;
        } return null;
    }

    /**
     * Overloaded method to take a screenshot of an app window with one preset for New World.
     * @param appWindowName contains the case-sensitive name of the window. The explicit name can be figured using the static method listAllWindows.
     * @param relativePath contains the relativePath to the root screenshot folder to save the screenshot in.
     * @param maxFiles states the maximum amount of files that can be made during one entire session.
     *                 -1 means there is no maximum amount specified.
     * @return File reference to the just made screenshot.
     */
    public static File takeScreenshotOfApp(String appWindowName, String relativePath, int maxFiles) {
        final String relativePath_ = relativePath + (relativePath.lastIndexOf("\\") != relativePath.length() - 1 ? "\\" : "") + appWindowName;
        if(appWindowName.contentEquals("New World")) {
            return takeScreenshotOfApp(appWindowName, relativePath_, maxFiles, 0.45, 0.45, 0.1, 0.05); //takeScreenshotOfApp(appWindowName, relativePath_, maxFiles, 2.2, 2.2, 10, 20);
        }
        return takeScreenshotOfApp(appWindowName, relativePath_, -1, 1, 1, 1, 1);
    }

    /**
     * Resizes an image to a target size
     * @param file image
     * @param maxWidth target width
     * @param maxHeight target height
     * @throws IOException ignored
     */
    public static void resize(File file,
                                int maxWidth, int maxHeight) throws IOException{
        int scaledWidth = 0, scaledHeight = 0;

        BufferedImage img = ImageIO.read(file);

        scaledWidth = maxWidth;
        scaledHeight = (int) (img.getHeight() * ( (double) scaledWidth / img.getWidth() ));

        if (scaledHeight> maxHeight) {
            scaledHeight = maxHeight;
            scaledWidth= (int) (img.getWidth() * ( (double) scaledHeight/ img.getHeight() ));

            if (scaledWidth > maxWidth) {
                scaledWidth = maxWidth;
                scaledHeight = maxHeight;
            }
        }

        Image resized =  img.getScaledInstance( scaledWidth, scaledHeight, Image.SCALE_SMOOTH);

        BufferedImage buffered = new BufferedImage(scaledWidth, scaledHeight, Image.SCALE_REPLICATE);

        buffered.getGraphics().drawImage(resized, 0, 0 , null);

        String formatName = getFormatName( file ) ;

        ImageIO.write(buffered,
                formatName,
                file);
    }

    private static String getFormatName(ImageInputStream iis) {
        try {

            // Find all image readers that recognize the image format
            Iterator iter = ImageIO.getImageReaders(iis);
            if (!iter.hasNext()) {
                // No readers found
                return null;
            }

            // Use the first reader
            ImageReader reader = (ImageReader)iter.next();

            // Close stream
            iis.close();

            // Return the format name
            return reader.getFormatName();
        } catch (IOException e) {
        }

        return null;
    }

    private static String getFormatName(File file) throws IOException {
        return getFormatName( ImageIO.createImageInputStream(file) );
    }

    private static String getFormatName(InputStream is) throws IOException {
        return getFormatName( ImageIO.createImageInputStream(is) );
    }

    private static int map(int x, int targetX) {
        if(x % targetX == 0) {
            return targetX;
        }
        double division = x/(double) targetX;
        int ceilDivision = (int) Math.ceil(division);

        return x/ceilDivision;
    }

    private static void listAllWindows() throws AWTException, IOException {
        final List<WindowInfo> inflList = new ArrayList<WindowInfo>();
        final List<Integer> order = new ArrayList<Integer>();
        int top = User32.instance.GetTopWindow(0);
        while (top != 0) {
            order.add(top);
            top = User32.instance.GetWindow(top, User32.GW_HWNDNEXT);
        }

        User32.instance.EnumWindows(new WndEnumProc() {
            public boolean callback(int hWnd, int lParam) {
                WindowInfo info = getWindowInfo(hWnd);
                inflList.add(info);
                return true;
            }

        }, 0);
        Collections.sort(inflList, new Comparator<WindowInfo>() {
            public int compare(WindowInfo o1, WindowInfo o2) {
                return order.indexOf(o1.hwnd) - order.indexOf(o2.hwnd);
            }
        });
        for (WindowInfo w : inflList) {
            System.out.println(w);
        }
    }

    public static  WindowInfo getWindowInfo(int hWnd) {
        RECT r = new RECT();
        User32.instance.GetWindowRect(hWnd, r);
        byte[] buffer = new byte[1024];
        User32.instance.GetWindowTextA(hWnd, buffer, buffer.length);
        String title = Native.toString(buffer);
        WindowInfo info = new WindowInfo(hWnd, r, title);
        return info;
    }

    public static interface WndEnumProc extends StdCallLibrary.StdCallCallback {
        boolean callback(int hWnd, int lParam);
    }

    public static interface User32 extends StdCallLibrary {
        public static final String SHELL_TRAY_WND = "Shell_TrayWnd";
        public static final int WM_COMMAND = 0x111;
        public static final int MIN_ALL = 0x1a3;
        public static final int MIN_ALL_UNDO = 0x1a0;

        final User32 instance = (User32) Native.loadLibrary("user32", User32.class);

        boolean EnumWindows(WndEnumProc wndenumproc, int lParam);

        boolean IsWindowVisible(int hWnd);

        int GetWindowRect(int hWnd, RECT r);

        void GetWindowTextA(int hWnd, byte[] buffer, int buflen);

        int GetTopWindow(int hWnd);

        int GetWindow(int hWnd, int flag);

        boolean ShowWindow(int hWnd);

        boolean BringWindowToTop(int hWnd);

        int GetActiveWindow();

        boolean SetForegroundWindow(int hWnd);

        int FindWindowA(String winClass, String title);

        long SendMessageA(int hWnd, int msg, int num1, int num2);

        final int GW_HWNDNEXT = 2;
    }

    public static class RECT extends Structure {
        public int left, top, right, bottom;

        @Override
        protected List<String> getFieldOrder() {
            List<String> order = new ArrayList<>();
            order.add("left");
            order.add("top");
            order.add("right");
            order.add("bottom");
            return order;
        }
    }

    public static class WindowInfo {
        int hwnd;
        RECT rect;
        String title;

        public WindowInfo(int hwnd, RECT rect, String title) {
            this.hwnd = hwnd;
            this.rect = rect;
            this.title = title;
        }

        public String toString() {
            return String.format("(%d,%d)-(%d,%d) : \"%s\"", rect.left, rect.top, rect.right, rect.bottom, title);
        }
    }

}
