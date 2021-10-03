package OCR.newworld.queue.estimator;

import net.sourceforge.tess4j.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles the main part of getting the data, deriving needed information off it, and calculating estimations.
 */
public class Estimator {
    /**
     * Defines how often a screenshot is taken.
     */
    private int refreshIntervalsInMinutes = 2;

    /**
     * Defines the maximum threshold a queue position can fall or even rise mainly to filter out mistakes of the OCR.
     */
    private final int addingThreshold = 100;
    /**
     * Defines the maximum size of the queue position list.
     */
    private final int maxHistoryNodes = 20;

    /**
     * Contains every figured queue position of the size of maxHistoryNodes.
     */
    private final List<Integer> queuePosHistory = new ArrayList<>();

    /**
     * Contains the most recent estimation of queue time left.
     */
    private int estimatedTimeLeftInMinutes;

    /**
     * Standard constructor using the default refresh rate of two minutes
     */
    public Estimator() {}

    /**
     * Constructor using a custom refresh rate.
     * @param refreshIntervalsInMinutes refresh rate in integer minutes.
     */
    public Estimator(int refreshIntervalsInMinutes) {
        if(refreshIntervalsInMinutes > 0) {
            this.refreshIntervalsInMinutes = refreshIntervalsInMinutes;
        } else {
            throw new IllegalArgumentException("Refresh rate can't be 0!");
        }
    }

    /**
     * Derives the current queue position off a screenshot and filters some of the mistakes OCR might made.
     * @param image screenshot
     * @return Queue position
     * @throws TesseractException ignored
     */
    public int getQueuePosFromImage(File image) throws TesseractException{
        ITesseract instance = new Tesseract();
        instance.setDatapath("src\\main\\resources\\tessData");
        instance.setTessVariable("debug_file", "/dev/null");
        instance.setTessVariable("tessedit_char_whitelist", "0123456789");

        /* IMPORTANT: specifies the file name with a traineddata file extension. Changing this to 'digits' for example may or
        *             may not help getting a better recognition. Creating a .traineddata based on the New World font dataset might
        *             decrease the loss dramatically in future.
        */
        instance.setLanguage("eng");

        String imgText = instance.doOCR(image);
        System.out.println("Recognized text: " + imgText);

        if(!imgText.contains("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstovwxyz.,-;:_?!")) {
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(imgText);
            if (matcher.find()) {
                imgText = matcher.group();

                try {
                    return Integer.parseInt(imgText);
                } catch (NumberFormatException ex) {
                    System.err.println("Could not parse to Int: " + ex.getMessage());
                }
            }
        }
        return -1;
    }

    /**
     * Calculates the time left in minutes queueing
     * @return queue time left in minutes
     * @throws RuntimeException if none or only one queue position is known.
     */
    private int estimateTimeLeftInMinutes() throws RuntimeException {
        int averageForwarding = 0;

        if(queuePosHistory.size() > 1) {
            for (int i = 0; i < queuePosHistory.size() - 1; i++) {
                averageForwarding += queuePosHistory.get(i) - queuePosHistory.get(i+1);
            } averageForwarding /= queuePosHistory.size()-1;
            return (queuePosHistory.get(queuePosHistory.size()-1)/averageForwarding)*refreshIntervalsInMinutes;
        } else {
            throw new RuntimeException("At least two different queue positions are required to calculate an estimation.");
        }
    }

    /**
     * Getter of queuePosHistory
     * @return copy of queuePosHistory
     */
    public List<Integer> getQueuePosHistory() {
        return new ArrayList<>(queuePosHistory);
    }

    /**
     * Handles adding a new queue position to the queue history list.
     * It also regulates the size of the queue history list and applies the addingThreshold to filter probably invalid
     * positions.
     * After adding a valid queue position to the list, it tries to update estimatedTimeLeftInMinutes.
     * @param pos The queue position to be added.
     *            -1 means a queue position in invalid.
     */
    public void addPosToHistory(int pos) {
        if(pos != -1) {
            if (queuePosHistory.size() >= maxHistoryNodes) {
                queuePosHistory.remove(0);
            }

            if(queuePosHistory.size() != 0)
                if((queuePosHistory.get(queuePosHistory.size()-1) - pos > addingThreshold) ||
                        (pos - queuePosHistory.get(queuePosHistory.size()-1) > addingThreshold))
                    throw new IllegalArgumentException("The queue position cannot rise or fall more than 100 positions at a time!");

            queuePosHistory.add(pos);
            try {
                estimatedTimeLeftInMinutes = estimateTimeLeftInMinutes();
            } catch (RuntimeException runtimeException) {
                System.err.println(runtimeException.getMessage());
            }


        } else {
            if(queuePosHistory.size() != 0) addPosToHistory(queuePosHistory.get(queuePosHistory.size()-1));
        }
    }

    /**
     * Getter of estimatedTimeLeftInMinutes
     * @return estimatedTimeLeftInMinutes
     */
    public int getEstimatedTimeLeftInMinutes() {
        return estimatedTimeLeftInMinutes;
    }
    /**
     * Getter of refreshIntervalsInMinutes
     * @return refreshIntervalsInMinutes
     */
    public int getRefreshIntervalsInMinutes() {
        return refreshIntervalsInMinutes;
    }
}
