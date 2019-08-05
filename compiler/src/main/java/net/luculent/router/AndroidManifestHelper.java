package net.luculent.router;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by xiayanlei on 2018/8/16.
 */

public class AndroidManifestHelper {

    private static final String manifestDir = "intermediates/manifests";
    private static final String aptXml = "AndroidManifest.xml";

    /**
     * a trick make a fake file to locate manifest
     *
     * @param filer
     * @return
     * @throws IOException
     */
    public static File getManifestDir(Filer filer) throws IOException {
        FileObject dummyFile = filer.createResource(StandardLocation.SOURCE_OUTPUT, "", "dummy" + System
                .currentTimeMillis());
        String dummyFilePath = dummyFile.toUri().toString();
        String buildPath = dummyFilePath.substring(0, dummyFilePath.indexOf("generated"));
        String manifestDirPath = buildPath + manifestDir;
        if (manifestDirPath.startsWith("file:")) {
            if (!manifestDirPath.startsWith("file://")) {
                manifestDirPath = "file://" + manifestDirPath.substring("file:".length());
            }
        } else {
            manifestDirPath = "file://" + manifestDirPath;
        }
        URI cleanUri;
        try {
            cleanUri = new URI(manifestDirPath);
        } catch (URISyntaxException e) {
            throw new FileNotFoundException();
        }
        return new File(cleanUri);
    }

    public static void createAptManifest(File manifestDir, Set<Element> elements) {
        if (elements.isEmpty()) {
            return;
        }
        DocumentBuilderFactory domFac = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder domBuilder = domFac.newDocumentBuilder();
            Document document = domBuilder.newDocument();
            document.setXmlStandalone(true);
            org.w3c.dom.Element rootEle = document.createElement("manifest");
            rootEle.setAttribute("xmlns:android", "http://schemas.android.com/apk/res/android");
            org.w3c.dom.Element application = document.createElement("application");
            for (Element ele : elements) {//add activity
                MActivity mActivity = ele.getAnnotation(MActivity.class);
                org.w3c.dom.Element activity = document.createElement("activity");
                activity.setAttribute("android:name", ((TypeElement) ele).getQualifiedName().toString());
                activity.setAttribute("android:launchMode", mActivity.launchMode());
                activity.setAttribute("android:screenOrientation", mActivity.orientation());
                application.appendChild(activity);
            }
            rootEle.appendChild(application);
            document.appendChild(rootEle);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            File manifestFile = new File(manifestDir, aptXml);
            if (!manifestFile.exists()) {
                transformer.transform(new DOMSource(document), new StreamResult(manifestFile));
            }
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }
}
