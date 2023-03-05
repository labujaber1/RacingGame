package DualRace;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Title: Distributed multi-player racing game.
 * Date: 21/04/2023
 * @author labuj 2018481
 * Description: Two player car race game using client server setup.
 */
public class SoundDR {
    private URL m_crashGreen,m_crashPolice,m_crashCars,m_cheer,m_countdown;
    //private String m_crashGreen,m_crashPolice,m_crashCars,m_cheer;
    public SoundDR() {
        m_crashGreen = this.getClass().getClassLoader().getResource("Sounds/fast-collision.wav");
        m_crashPolice = this.getClass().getClassLoader().getResource("Sounds/clank-car-crash.wav");
        m_crashCars = this.getClass().getClassLoader().getResource("Sounds/squish.wav");
        m_cheer = this.getClass().getClassLoader().getResource("Sounds/cheer.wav");
        m_countdown = this.getClass().getClassLoader().getResource("Sounds/countdown.wav");
    }
    public void crashGreen()
    {
        Play(m_crashGreen);
    }
    public void crashPolice()
    {
        Play(m_crashPolice);
    }
    public void crashCars()
    {
        Play(m_crashCars);
    }
    public void cheer()
    {
        Play(m_cheer);
    }
    public void countdown() { Play(m_countdown);}

    /**
     * Play sound on separate thread and stop if interrupted such as high speed noise.
     * @param url - Sound file URL path
     */
    private void Play(URL url){
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if(event.getType() == LineEvent.Type.STOP)
                        clip.close();
                }

            });
            audioIn.close();
            clip.start();
        }catch (IOException | LineUnavailableException | UnsupportedAudioFileException ex){
            System.err.println("Error reading file: "+ ex.getMessage());
        }
    }



    private InputStream m_inputStream;
    private AudioInputStream m_audioStream;
    private AudioFormat m_audioFormat;
    private static final int m_BUFFER_SIZE = 4096;

    public void Play2(String fileName){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    m_inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
                    if (m_inputStream != null) {
                        System.out.println("Sound file path ok");
                        m_audioStream = AudioSystem.getAudioInputStream(m_inputStream); // error format
                        m_audioFormat = m_audioStream.getFormat();
                        DataLine.Info info = new DataLine.Info(SourceDataLine.class, m_audioFormat);
                        SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
                        sourceDataLine.open(m_audioFormat);
                        sourceDataLine.start();
                        byte[] bufferBytes = new byte[m_BUFFER_SIZE];
                        int readBytes = -1;
                        while ((readBytes = m_audioStream.read(bufferBytes)) != -1) {
                            sourceDataLine.write(bufferBytes, 0, readBytes);
                        }
                        sourceDataLine.drain();
                        sourceDataLine.close();
                        m_audioStream.close();
                    } else System.out.println("Sound file path not found");
                } catch (UnsupportedAudioFileException |
                         IOException e) {
                    System.out.println("Error reading getting audio stream: " + e.getMessage());
                } catch (
                        LineUnavailableException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }



}
