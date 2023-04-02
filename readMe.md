# Audio Restoration Program

This project is an audio restoration program that reduces noise and balances frequency response in degraded or poorly recorded audio clips. The program uses Wiener filtering for noise reduction and parametric equalization for frequency response adjustment.

## Prerequisites

Ensure that you have the following software installed on your machine:

- Java Development Kit (JDK) 8 or later: [Download JDK](https://www.oracle.com/java/technologies/javase-jdk14-downloads.html)
- Apache Maven: [Download Maven](https://maven.apache.org/download.cgi)

## Installation

1. Clone the repository to your local machine:

```bash
git clone https://github.com/William-Letshu/audio_restoration.git
cd audio_restoration-main
mvn clean install
mvn exec:java -Dexec.mainClass="com.example.project_1.HelloApplication"
```


Follow the prompts to provide the necessary input parameters, such as input audio file, output file name, and processing options. The program will process the audio file and save the restored output in the specified location.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
