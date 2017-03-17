import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

/**
 * Created by Miguel on 2/27/2017.
 */
public class DecisionTree {


    static Document parse(String file) throws ParserConfigurationException, SAXException, IOException{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        return doc;
    }

    static Element returnRandomChild (Element e){
        NodeList list = e.getChildNodes();
        List<Element> pos = new ArrayList<>();
        for(int i = 0; i < list.getLength(); i++){
            if(list.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element temp = (Element) list.item(i);
                pos.add(temp);
            }
        }

        for(int i = 0; i < pos.size(); i++){
            if(pos.get(i).hasChildNodes())
                return pos.get(0);
        }

        int random = (int) (Math.random() * pos.size());
        return pos.get(random);

    }

    static List BFS(Document doc, String target){
        List<String> answer = new ArrayList<>();
        int count = 1;
        Element root = doc.getDocumentElement();
        Queue<Element> queue = new LinkedList<>();
        queue.add(root);
        while(!queue.isEmpty() && !(root.getAttribute("behavior").equals(target))){
            root = queue.poll();
            if(root.hasChildNodes()){
                NodeList childs = root.getChildNodes();
                for(int i = 0; i < childs.getLength(); i++){
                    if(childs.item(i).getNodeType() == Node.ELEMENT_NODE){
                        Element element = (Element) childs.item(i);
                        queue.add(element);
                    }
                }
            }
            count++;
        }
        if(queue.isEmpty()){
            return answer;
        }
        Element child = returnRandomChild(root);
        if(child.getAttribute("response").isEmpty()){
            NodeList nodes = child.getParentNode().getChildNodes();
            for(int i = 0; i < nodes.getLength(); i++){
                if(nodes.item(i).getNodeType() == Node.ELEMENT_NODE){
                    Element elem = (Element) nodes.item(i);
                    answer.add(elem.getAttribute("behavior"));
                }
            }
        } else {
            answer.add(child.getAttribute("response"));
        }
        answer.add(Integer.toString(count));
        return answer;
    }

    static List DFS(Document doc, String target){
        List<String> answer = new ArrayList<>();
        int count = 1;
        Element root = doc.getDocumentElement();
        Stack<Element> stack = new Stack<>();
        stack.push(root);
        while(!stack.isEmpty() && !(root.getAttribute("behavior").equals(target))){
            root = stack.pop();
            if(root.hasChildNodes()){
                NodeList childs = root.getChildNodes();
                for(int i = childs.getLength(); i > 0; i--){
                    if(childs.item(i - 1).getNodeType() == Node.ELEMENT_NODE){
                        Element element = (Element) childs.item(i - 1);
                        stack.push(element);
                    }
                }
            }
            count++;
        }
        if(stack.isEmpty()){
            return answer;
        }
        Element child = returnRandomChild(root);
        if(child.getAttribute("response").isEmpty()){
            NodeList nodes = child.getParentNode().getChildNodes();
            for(int i = 0; i < nodes.getLength(); i++){
                if(nodes.item(i).getNodeType() == Node.ELEMENT_NODE){
                    Element elem = (Element) nodes.item(i);
                    answer.add(elem.getAttribute("behavior"));
                }
            }
        } else {
            answer.add(child.getAttribute("response"));
        }
        answer.add(Integer.toString(count));
        return answer;
    }

    static void printTree(Document doc){
        Element root = doc.getDocumentElement();
        NodeList nodes = doc.getElementsByTagName("node");
        System.out.println("behavior = " + root.getTagName());
        for(int i = 0; i < nodes.getLength(); i++){
            Node node = nodes.item(i);
            Element nodeElement = (Element) node;
            if(!nodeElement.getAttribute("behavior").isEmpty()){
                if(nodeElement.getParentNode() == root){
                    System.out.println("\tbehavior= " + nodeElement.getAttribute("behavior"));
                }else{
                    System.out.println("\t\tbehavior= " + nodeElement.getAttribute("behavior"));
                }
            } else if(!nodeElement.getAttribute("response").isEmpty()){
                if(nodeElement.getParentNode().getParentNode() == root){
                    System.out.println("\t\tresponse= " + nodeElement.getAttribute("response"));
                } else{
                    System.out.println("\t\t\tresponse= " + nodeElement.getAttribute("response"));
                }
            }
        }

    }

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        String target = "";
        List<String> bfs;
        List<String> dfs;


        Scanner input = new Scanner(System.in);

        System.out.println("Scanning XML File");
        System.out.println("================================");
        Document doc = parse("sample.xml");
        printTree(doc);
        System.out.println("================================");
        System.out.print("Event ('quit' to exit) : ");
        target = input.nextLine();
        while(!target.equals("quit")){
            bfs = BFS(doc, target);
            dfs = DFS(doc, target);
            if(bfs.isEmpty()){
                System.out.println("Value is not in tree");
            }
            else if(bfs.size() > 2){
                System.out.println("Choose between these behaviors: " + bfs.get(0) + " and " + bfs.get(1));
            } else {
                System.out.println("Response = " + bfs.get(0));
                System.out.println("Target found in " + bfs.get(1) + " steps with BFS, " + dfs.get(1) + " steps with DFS");
            }
            System.out.print("Event ('quit' to exit) : ");
            target = input.nextLine();
        }

    }
}
