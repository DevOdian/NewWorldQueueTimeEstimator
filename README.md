# newWorldQueueTimeEstimator
Linearly estimates the current time left to wait until joining a New World (Game) session. The queue positions are received using the [Tesseract OCR API](https://github.com/tesseract-ocr/tesseract).

## My personal non-binding building environment conditions
- IntelliJ IDEA 2020.2.2
- openJDK 17 (But was originally built with openJDK 15)
- Maven 3.6.3 dependencies:
  - tess4J 4.5.5
  - slf4j 1.7.32

## Important Notes
### Main
The main method for estimation lies in `OCR.newworld.queue.estimator.MainEstimator`.

### Cropping
The `ScreenshotHandler` has a preset to crop the New World application window to just the queue position number. Check the `./screenshots/New World` folder for the exact data I ran on. 3 .JPG image files are standardly stored in here max.

The preset parameters for cropping screenshots of New World might not fit your system. See the class `OCR.newworld.screenshot.ScreenshotHandler` and overloaded method `File takeScreenshotOfApp(...)` to adjust the cropping parameters for New World.

### OCR Mistakes
Especially because Tesseract is running with no matching `.traineddata` for New World's font, mistakes occur and are more probable the higher the queue position numbers are. Mostly, though, are queue position numbers in doubt filtered out.  

## External Sources
Tesseract OCR trained data (tessData):
- *eng.traineddata*, *deu.traineddata*: [tesseract-ocr/tessdata.git](https://github.com/tesseract-ocr/tessdata.git)
- *digits.traineddata*: [Shreeshrii/tessdata_shreetest.git](https://github.com/Shreeshrii/tessdata_shreetest.git)

Code base to make screenshots of specific window: [StackOverflow: wutzebaer](https://stackoverflow.com/a/47182139)
