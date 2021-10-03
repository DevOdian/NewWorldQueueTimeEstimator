package OCR.newworld.queue.estimator;

import OCR.newworld.screenshot.ScreenshotHandler;

import java.io.File;

public class MainEstimator {
    public static void main(String[] args) throws InterruptedException {
        int refreshIntervalsInMinutes = 1;
        Estimator estimator = new Estimator(refreshIntervalsInMinutes);
        int pos = -1;

        /* Infinite loop */
        while(!Thread.interrupted()) {
            try {
                /* Take screenshot of New World cropped to the queue position number */
                File screenshot = ScreenshotHandler.takeScreenshotOfApp("New World", "screenshots", 3);
                if(screenshot != null) {
                    /* Derive queue position number from screenshot */
                    pos = estimator.getQueuePosFromImage(screenshot);
                    /* Add derived queue position to queue position history list */
                    estimator.addPosToHistory(pos);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

            /* Get current estimation in minutes */
            int currentEstimatedTime = estimator.getEstimatedTimeLeftInMinutes();
            /* Map minutes to hours and minutes left */
            int hours = currentEstimatedTime/60, minutes = currentEstimatedTime%60;

            /* Console output */
            System.out.print("Queue Histroy {");
            estimator.getQueuePosHistory().forEach(x-> System.out.print(x + ", "));
            System.out.println("}");
            System.out.println("Current Position: " + (estimator.getQueuePosHistory().size() != 0 && pos != -1 ? estimator.getQueuePosHistory().get(estimator.getQueuePosHistory().size() - 1) : "Not recognized"));
            System.out.println("Estimated time until join: " +
                    hours + " hours, " + minutes + " minutes" + "\n");


            /* Sleep for refresh rate */
            Thread.sleep(minutesToMillis(refreshIntervalsInMinutes));
        }
    }

    /**
     * Converts minutes into milliseconds
     * @param minutes minutes
     * @return milliseconds
     */
    public static long minutesToMillis(double minutes) {
        return (long) (minutes*1000*60);
    }
}
