package com.edwin.shakautils.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * 资源加载工具类
 * 
 * @author jinming.wu
 * @date 2015-5-20
 */
public class ResourceHelper {

    public static final String        CLASSPATH_URL_PREFIX = "classpath:";

    public static final String        FILE_URL_PREFIX      = "file:";

    private static ClassLoaderWrapper classLoaderWrapper   = new ClassLoaderWrapper();

    public static URL getURL(String resource) throws FileNotFoundException {
        return getURL(resource, null);
    }

    public static InputStream getInputStream(String resource) throws FileNotFoundException {
        return getInputStream(resource, null);
    }

    public static Properties getProperties(String resource) throws IOException {
        return getProperties(resource, null);
    }

    public static Reader getReader(String resource) throws IOException {
        return new InputStreamReader(getInputStream(resource));
    }

    public static Reader getReader(String resource, String charset) throws IOException {

        Reader reader = null;

        if (charset == null) {
            reader = new InputStreamReader(getInputStream(resource));
        } else {
            reader = new InputStreamReader(getInputStream(resource), charset);
        }

        return reader;
    }

    /**
     * 解析资源为Reader对象
     * 
     * @param resource
     * @param classLoader
     * @param charset
     * @return
     * @throws IOException
     */
    public static Reader getReader(String resource, ClassLoader classLoader, String charset) throws IOException {

        Reader reader = null;

        if (charset == null) {
            reader = new InputStreamReader(getInputStream(resource, classLoader));
        } else {
            reader = new InputStreamReader(getInputStream(resource, classLoader), charset);
        }

        return reader;
    }

    /**
     * 解析资源为Properties对象
     * 
     * @param resource
     * @param classLoader
     * @return
     * @throws IOException
     */
    public static Properties getProperties(String resource, ClassLoader classLoader) throws IOException {

        Properties props = new Properties();

        InputStream in = getInputStream(resource, classLoader);

        props.load(in);

        in.close();

        return props;
    }

    /**
     * 根据resource location获取input stream
     * 
     * @param resource
     * @param classLoader
     * @return
     * @throws FileNotFoundException
     * @throws MalformedURLException
     */
    public static InputStream getInputStream(String resource, ClassLoader classLoader) throws FileNotFoundException {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(resource), "Resource location must not be null!");

        if (resource.startsWith(CLASSPATH_URL_PREFIX)) {

            String path = resource.substring(CLASSPATH_URL_PREFIX.length());

            InputStream inputStream = classLoader == null ? classLoaderWrapper.getStream(path) : classLoaderWrapper.getStream(resource,
                                                                                                                              classLoader);
            if (inputStream != null) {
                return inputStream;
            }
            throw new FileNotFoundException("Classpath resource [" + path
                                            + "] cannot be resolved to InputStream because it does not exist");
        }

        try {
            return getURL(resource).openConnection().getInputStream();
        } catch (IOException e) {
            throw new FileNotFoundException("Resource [" + resource + "] is neither a URL not a valid file path");
        }
    }

    /**
     * 根据resource location转换成URL
     * 
     * @param resource
     * @param classLoader
     * @return
     * @throws FileNotFoundException
     */
    public static URL getURL(String resource, ClassLoader classLoader) throws FileNotFoundException {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(resource), "Resource location must not be null!");

        if (resource.startsWith(CLASSPATH_URL_PREFIX)) {

            String path = resource.substring(CLASSPATH_URL_PREFIX.length());

            URL url = classLoader == null ? classLoaderWrapper.getURL(path) : classLoaderWrapper.getURL(resource,
                                                                                                        classLoader);
            if (url != null) {
                return url;
            }
            throw new FileNotFoundException("Classpath resource [" + path
                                            + "] cannot be resolved to URL because it does not exist");
        }

        try {
            return new URL(resource);
        } catch (MalformedURLException e1) {
            try {
                return new File(resource).toURI().toURL();
            } catch (MalformedURLException e2) {
                throw new FileNotFoundException("Resource [" + resource
                                                + "] is neither a URL not a well-formed file path");
            }
        }
    }

}
