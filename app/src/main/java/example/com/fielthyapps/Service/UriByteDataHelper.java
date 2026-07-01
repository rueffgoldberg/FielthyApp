package example.com.fielthyapps.Service;

import android.net.Uri;
import android.util.Log;

import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DataSpec;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.extractor.DefaultExtractorsFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

class UriByteDataHelper {


    public Uri getUri(byte[] data) {

        try {
            URL url = new URL(null, "bytes:///" + "audio", new BytesHandler(data));
            return Uri.parse( url.toURI().toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    class BytesHandler extends URLStreamHandler {
        byte[] mData;
        public BytesHandler(byte[] data) {
            mData = data;
        }

        @Override
        protected URLConnection openConnection(URL u) throws IOException {
            return new ByteUrlConnection(u, mData);
        }
    }

    class ByteUrlConnection extends URLConnection {
        byte[] mData;
        public ByteUrlConnection(URL url, byte[] data) {
            super(url);
            mData = data;
        }

        @Override
        public void connect() throws IOException {
        }

        @Override
        public InputStream getInputStream() throws IOException {

            return new ByteArrayInputStream(mData);
        }
    }
}