# newWorldQueueTimeEstimator
Linearly estimates the current time left to wait until joining a New World (Game) session. The queue positions are received using the [Tesseract OCR API](https://github.com/tesseract-ocr/tesseract).

## My non-binding building environment requierements
- openJDK 17 (But was originally built with openJDK 15)
- Maven 3.6.3 dependencies:
  - tess4J 4.5.5
  - slf4j 1.7.32

## Sources
Tesseract OCR trained data (tessData):
- *eng.traineddata*, *deu.traineddata*: [tesseract-ocr/tessdata.git](https://github.com/tesseract-ocr/tessdata.git)
- *digits.traineddata*: [Shreeshrii/tessdata_shreetest.git](https://github.com/Shreeshrii/tessdata_shreetest.git)

Code base to make screenshots of specific window: [StackOverflow: wutzebaer](https://stackoverflow.com/a/47182139)
