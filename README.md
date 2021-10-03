# newWorldQueueTimeEstimator
Linearly estimates the current time left to wait until joining a locally running New World (Game) session. The queue positions are received using the [Tesseract OCR API](https://github.com/tesseract-ocr/tesseract).

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

### Exemplary Console Output
You can see below a snip out of an exemplary console output following the scheme: 
- The recognized text by the OCR
- The queue position history list
- The current figured position from the recognized text
- The estimation
- (Exception when e.g. OCR fails)

<details>
  <summary>Click to expand!</summary>
  
  ````
  Recognized text: 907

Queue Histroy {1039, 1013, 1013, 1003, 1003, 988, 982, 982, 972, 972, 964, 957, 957, 938, 938, 925, 925, 901, 907, 907, }
Current Position: 907
Estimated time until join: 2 hours, 31 minutes

Recognized text: 895

Queue Histroy {1013, 1013, 1003, 1003, 988, 982, 982, 972, 972, 964, 957, 957, 938, 938, 925, 925, 901, 907, 907, 895, }
Current Position: 895
Estimated time until join: 2 hours, 29 minutes

Recognized text: 885

Queue Histroy {1013, 1003, 1003, 988, 982, 982, 972, 972, 964, 957, 957, 938, 938, 925, 925, 901, 907, 907, 895, 885, }
Current Position: 885
Estimated time until join: 2 hours, 27 minutes

Recognized text: 885

Queue Histroy {1003, 1003, 988, 982, 982, 972, 972, 964, 957, 957, 938, 938, 925, 925, 901, 907, 907, 895, 885, 885, }
Current Position: 885
Estimated time until join: 2 hours, 27 minutes

The queue position cannot rise or fall more than 100 positions at a time!
Recognized text: 0

Queue Histroy {1003, 988, 982, 982, 972, 972, 964, 957, 957, 938, 938, 925, 925, 901, 907, 907, 895, 885, 885, }
Current Position: 885
Estimated time until join: 2 hours, 27 minutes

Recognized text: 857

Queue Histroy {1003, 988, 982, 982, 972, 972, 964, 957, 957, 938, 938, 925, 925, 901, 907, 907, 895, 885, 885, 857, }
Current Position: 857
Estimated time until join: 2 hours, 2 minutes

Recognized text: 847

Queue Histroy {988, 982, 982, 972, 972, 964, 957, 957, 938, 938, 925, 925, 901, 907, 907, 895, 885, 885, 857, 847, }
Current Position: 847
Estimated time until join: 2 hours, 1 minutes

Recognized text: 847

Queue Histroy {982, 982, 972, 972, 964, 957, 957, 938, 938, 925, 925, 901, 907, 907, 895, 885, 885, 857, 847, 847, }
Current Position: 847
Estimated time until join: 2 hours, 1 minutes

Recognized text: 843

Queue Histroy {982, 972, 972, 964, 957, 957, 938, 938, 925, 925, 901, 907, 907, 895, 885, 885, 857, 847, 847, 843, }
Current Position: 843
Estimated time until join: 2 hours, 0 minutes

Recognized text: 823

Queue Histroy {972, 972, 964, 957, 957, 938, 938, 925, 925, 901, 907, 907, 895, 885, 885, 857, 847, 847, 843, 823, }
Current Position: 823
Estimated time until join: 1 hours, 57 minutes
 ````
</details>

## External Sources
Tesseract OCR trained data (tessData):
- *eng.traineddata*, *deu.traineddata*: [tesseract-ocr/tessdata.git](https://github.com/tesseract-ocr/tessdata.git)
- *digits.traineddata*: [Shreeshrii/tessdata_shreetest.git](https://github.com/Shreeshrii/tessdata_shreetest.git)

Code base to make screenshots of specific windows: [StackOverflow: wutzebaer](https://stackoverflow.com/a/47182139)
