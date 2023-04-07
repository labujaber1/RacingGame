package DualRace;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

/**
 * Title: Distributed multi-player racing game.
 * Date: 21/04/2023
 * @author labuj 2018481
 * Description: Two player car race game using client server setup.
 */

/**
 * Plays audio clips passed in params when called from gamepanel.
 */
public class SoundDR {
    private URL m_url;
    private AudioInputStream m_audioIn;
    private Clip m_clip;

    /**
     * Pass in sound filepath and play file using javax.sound libraries
     * but must be .wav format.
     * Plays on a separate thread.
     * @param filename - sound file name and folder
     */
    public void Play(String filename){
        try {
            m_url = this.getClass().getClassLoader().getResource(filename);
            if(m_url != null) {
                m_audioIn = AudioSystem.getAudioInputStream(m_url);
                m_clip = AudioSystem.getClip();
                m_clip.open(m_audioIn);
                m_clip.addLineListener(new LineListener() {
                    @Override
                    public void update(LineEvent event) {
                        if (event.getType() == LineEvent.Type.STOP)
                            m_clip.close();
                    }

                });
                m_audioIn.close();
                m_clip.start();
            }
            else {
                System.out.println("Cannot find sound file");
            }
        }catch (IOException | LineUnavailableException | UnsupportedAudioFileException ex){
            System.err.println("Error reading file: "+ ex.getMessage());
        }
    }

}




