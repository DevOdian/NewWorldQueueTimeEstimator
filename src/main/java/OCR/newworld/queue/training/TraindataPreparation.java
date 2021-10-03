package OCR.newworld.queue.training;

import OCR.newworld.screenshot.ScreenshotHandler;
import OCR.newworld.queue.estimator.MainEstimator;

import java.io.File;

public class TraindataPreparation {
    /**
     * Only takes screenshots for every 1.5 minutes and saves them into /screenshots/New World/train for now.
     * @param args ignored
     * @throws InterruptedException ignored
     */
    public static void main(String[] args) throws InterruptedException {
        while(!Thread.interrupted()) {
            File file= ScreenshotHandler.takeScreenshotOfApp("New World", "screenshots\\trainData", -1);
            System.out.println("File created at: " + file.getPath());

            Thread.sleep(MainEstimator.minutesToMillis(1.5));
        }
    }
}
