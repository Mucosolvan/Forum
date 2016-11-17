package pl.edu.mimuw.forum.xstream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Reader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import pl.edu.mimuw.forum.data.Node;
import pl.edu.mimuw.forum.ui.models.NodeViewModel;

public class FileHandling {

	private static XStream xStream = new XStream(new DomDriver("Unicode"));

	public static void saveToFile(File file, Node node) throws IOException {
		PrintWriter writer = new PrintWriter(file, "UTF-8");
		xStream.addImplicitCollection(Node.class, "children");
		ObjectOutputStream out = xStream.createObjectOutputStream(writer, "Forum");
		out.writeObject(node);
		out.close();
	}

	public static NodeViewModel openFile(File file) {
		Reader rdr;
		try {
			rdr = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			ObjectInputStream in = xStream.createObjectInputStream(rdr);
			xStream.addImplicitCollection(Node.class, "children");
			Node node = (Node) in.readObject();
			in.close();
			return node.getModel();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
