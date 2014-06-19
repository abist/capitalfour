capitalfour
===========

Worked in a team of four at Capital One's first ever developer summit for software engineers. After completing a national coding challenge, the top 25 applicants were flown to Capital One's headquarters (Washington DC) to attend a one week long hackathon. Roughly 24 hours was given to the coding portion where the four of us created an Android application.

This Android application followed the education theme of the hackathon. The application was intended to empower third-world countries where computers are not as readily available but smartphones and tablets are, the user would open the Android application, take a picture of handwritten code and be presented with the output or compile errors. Many other use cases became notable as we continued to ponder our solution, for instance, handwritten programming tets could be more efficiently graded or with a device such as Google Glass, a programmer could visualize output on a piece of paper while writing it.

This project was accomplished by saving a picture to the Android device, POSTing it to a Heroku server we created where Google's Tesseract would convert the picture into text. This text was then sent to codepad.org (after following the network traffic we figured out how to trick it into believing we were a user), took that output and sent it back to the Android device to be presented.

All of the languages on codepad.org are supported (currently 13.)


Team Members: Spencer Shadley, Aditya Bist, Aabhas Sharma and Nick Grifka
