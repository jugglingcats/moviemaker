package com.akirkpatrick.mm.generator;

import com.akirkpatrick.mm.FileHelper;
import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.jcodec.scale.RgbToYuv420;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alfie
 * Date: 22/09/13
 * Time: 22:11
 * To change this template use File | Settings | File Templates.
 */
public class MovieGenerator2 {
    public void create(List<String> frames, OutputStream output) {
        H264Encoder encoder=new H264Encoder();
        RgbToYuv420 transform=new RgbToYuv420(0, 0);
        try {
            for ( String s : frames ) {
                File f= new File(s);
                BufferedImage i= ImageIO.read(f);
                Picture yuv=Picture.create(i.getWidth(), i.getHeight(), ColorSpace.YUV420);
                transform.transform(AWTUtil.fromBufferedImage(i), yuv);
                ByteBuffer buf= ByteBuffer.allocate(i.getWidth() * i.getHeight() * 3);
                ByteBuffer ff=encoder.encodeFrame(buf, yuv);
                WritableByteChannel channel = Channels.newChannel(output);
                channel.write(ff);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
