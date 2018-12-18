# MelonAI

We set out to create an impactful solution for anyone who can benefit from improved accessibility to everyday sound events.  Our mobile application uses artificial intelligence to recognize key sound events of interest to the community such as emergency vehicle sirens and door knocks where immediate alerts and continuous logging is critical for the user.  While there are many audio accessibility innovations in the app space, up until the time of writing it has been mostly in the areas of sound amplification and text to speech/speech to text.  This app is optimized for Android with low-latency so that it works in real-time for the user. 

The Melon AI app converts a sound wave (from the mic) into a mel-spectrogram image that serves as the main feature fed into a Convolutional Neural Network that will then classify the sound into one of eight classes. Average inference time is about 15 ms so the user never has to worry about missing a beat and the app can also be synced with a wearable device.

Coming soon to Google Play

#### UI Screenshots

| ![menu](https://github.com/stp8954/AwesomeAI/blob/master/image/image1.jpg) | ![activated](https://github.com/stp8954/AwesomeAI/blob/master/image/image2.jpg) | ![prediction](https://github.com/stp8954/AwesomeAI/blob/master/image/image3.jpg) |
| - | - | - |
| ![settings](https://github.com/stp8954/AwesomeAI/blob/master/image/image4.jpg) | ![event selector](https://github.com/stp8954/AwesomeAI/blob/master/image/image5.jpg) | |

## Pipeline Overview

![pipeline](https://github.com/stp8954/AwesomeAI/blob/master/image/pipeline.png)

## Performance Overview

 - 110 MB Peak Memory Usage
 - 5% Average, 10% Peak CPU Usage
 - 10-15% Battery Life Penalty
 
Algorithmic performance:

![pipeline](https://github.com/stp8954/AwesomeAI/blob/master/image/performance.png)

## Suggested Contributions

1. Enable "wake word" detection based on user's name
2. Cross-platform support
3. Sensitivity (threshold tuning)
4. General accuracy improvements with minimal power usage penalty

## Project Co-founders
 - [Sanket Patel](https://github.com/stp8954)
 - [@sarahcha07](https://github.com/sarahcha07)
 - [@ramagana](https://github.com/ramagana)
 - [Bas Hendri](https://github.com/dipidoo)
