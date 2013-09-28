package com.akirkpatrick.mm.generator;

import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.media.*;
import javax.media.control.TrackControl;
import javax.media.datasink.DataSinkErrorEvent;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

public class MovieGenerator implements ControllerListener, DataSinkListener {

    private Processor processor;
    private Object sync = new Object();
    private boolean success;
    private Exception error;
    private final MediaLocator mediaLocator = new MediaLocator("file:"+UUID.randomUUID().toString()+".mpg");

    public void create(List<String> frames, OutputStream output) {

        assert (frames.size() > 0);
        try {
            BufferedImage firstImage = ImageIO.read(new File(frames.get(0)));
            int width = firstImage.getWidth();
            int height = firstImage.getHeight();

            int framerate = 7;

            ImageDataSource ids = new ImageDataSource(width, height, framerate, frames);
            processor = Manager.createProcessor(ids);
            processor.addControllerListener(this);
            synchronized (sync) {
                processor.configure();
                sync.wait(5000);
            }
            processor.stop();
            processor.close();

            if ( success ) {
                File result=new File(mediaLocator.getURL().getPath());
                FileUtils.copyFile(result, output);
                result.delete();
            }

        } catch (Exception e) {
            error=e;
        } finally {
            if ( !success ) {
                if ( error != null ) {
                    throw new RuntimeException(error);
                } else {
                    throw new RuntimeException("An unknown error occurred or processing took longer than expected");
                }
            }
        }
    }

    @Override
    public void controllerUpdate(ControllerEvent event) {
        if (event instanceof ConfigureCompleteEvent) {
            configureComplete();
        } else if (event instanceof RealizeCompleteEvent) {
            realizeComplete();
        } else if (event instanceof ResourceUnavailableEvent) {
            error = new Exception(((ResourceUnavailableEvent) event).getMessage());
            notifyDone();
        } else if (event instanceof EndOfMediaEvent) {
            event.getSourceController().stop();
            event.getSourceController().close();
            success = true;
            notifyDone();
        }
    }

    private void notifyDone() {
        synchronized (sync) {
            sync.notifyAll();
        }
    }

    private void configureComplete() {
        processor.setContentDescriptor(new ContentDescriptor(FileTypeDescriptor.QUICKTIME));

        TrackControl[] trackControls = processor.getTrackControls();
        Format[] supportedFormats = trackControls[0].getSupportedFormats();
        assert (supportedFormats != null);
        assert (supportedFormats.length > 0);
        trackControls[0].setFormat(supportedFormats[0]);

        processor.realize();
    }

    private void realizeComplete() {
        DataSource dataSource = processor.getDataOutput();
        assert (dataSource != null);
        try {
            DataSink dataSink = Manager.createDataSink(dataSource, mediaLocator);
            dataSink.addDataSinkListener(this);
            dataSink.open();

            processor.start();
            dataSink.start();
        } catch (NoDataSinkException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void dataSinkUpdate(DataSinkEvent event) {
        try {
            if (event instanceof EndOfStreamEvent) {
//                success = true;
                event.getSourceDataSink().stop();
                event.getSourceDataSink().close();
            } else if (event instanceof DataSinkErrorEvent) {
                error = new Exception("Data sink error occurred!");
            }
        } catch (IOException e) {
            // nothing to be done
        } finally {
            notifyDone();
        }
    }
}
